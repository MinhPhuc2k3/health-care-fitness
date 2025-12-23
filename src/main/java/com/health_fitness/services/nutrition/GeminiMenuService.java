package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.*;
import com.health_fitness.model.user.User;
import com.health_fitness.repository.nutrition.ChatHistoryRepository;
import com.health_fitness.repository.nutrition.IngredientRepository;
import com.health_fitness.repository.nutrition.MenuRepository;
import com.health_fitness.repository.nutrition.RecipeRepository;
import com.health_fitness.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiMenuService {

    private final ChatClient chatClient;
    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;
    private final MenuPlanService menuPlanService;
    private final MenuService menuService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserService userService;
    private final BeanOutputConverter<AiMenuSuggestionResponse> converter = new BeanOutputConverter<>(AiMenuSuggestionResponse.class);

    public GeminiMenuService(
            ChatClient.Builder chatClientBuilder,
            RecipeRepository recipeRepository,
            MenuService menuService,
            MenuRepository menuRepository,
            MenuPlanService menuPlanService,
            ChatHistoryRepository chatHistoryRepository,
            UserService userService) {

        // Khởi tạo ChatClient từ Builder
        this.chatClient = chatClientBuilder.build();
        this.recipeRepository = recipeRepository;
        this.menuRepository = menuRepository;
        this.menuService = menuService;
        this.menuPlanService = menuPlanService;
        this.chatHistoryRepository = chatHistoryRepository;
        this.userService = userService;
    }
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ChatHistory generateDailyMenuSuggestion(String userPrompt) {
        User user = userService.getUser();

        // 1. Lấy mục tiêu dinh dưỡng từ MenuPlan
        MenuPlan targetPlan = menuPlanService.getMenuPlanToday();

        // 2. Lấy lịch sử ăn uống & chat gần nhất
        List<Menu> history = menuRepository.findTop5ByCreatedByOrderByCreatedDateDesc(user);
        List<ChatHistory> pastChats = chatHistoryRepository.findTop3ByCreatedByOrderByIdDesc(user);

        // 3. Lấy danh sách Recipe khả dụng (chọn các field cần thiết để tiết kiệm token)
        List<Recipe> availableRecipes = recipeRepository.findAll();
        String recipeData = availableRecipes.stream()
                .map(r -> String.format("{id:%d, name:'%s', cal:%f, p:%f, c:%f, f:%f}",
                        r.getId(), r.getName(), r.getCalories(), r.getProtein(), r.getCarbs(), r.getFat()))
                .collect(Collectors.joining(","));

        // 4. Cấu hình Output Converter

        // 5. Xây dựng Prompt tổng hợp
        String systemPrompt = """
                Bạn là một chuyên gia dinh dưỡng thông minh.
                NHIỆM VỤ: Tạo thực đơn 1 ngày (Menu) dựa trên dữ liệu sau:
                1. MỤC TIÊU: Calo: %.1f, Protein: %.1f, Carb: %.1f, Fat: %.1f.
                2. DANH SÁCH MÓN ĂN ĐƯỢC PHÉP DÙNG: [%s]
                
                QUY TẮC:
                - Phải có đủ 4 bữa: BREAKFAST, LUNCH, DINNER, SNACK.
                - Tổng dinh dưỡng các món phải xấp xỉ MỤC TIÊU (sai số < 5%%).
                - Không được lặp lại các món ăn có tên tương tự cho các bữa trong ngày
                - Phải trả về JSON khớp cấu trúc sau:
                %s
                """.formatted(targetPlan.getTargetCalories(), targetPlan.getTargetProtein(),
                targetPlan.getTargetCarb(), targetPlan.getTargetFat(),
                recipeData, converter.getFormat());

        systemPrompt += """
                Ngày đề xuất: %s
                Sở thích từ lịch sử: %s
                Lưu ý từ các cuộc trò chuyện trước: %s
                Hãy gợi ý thực đơn tốt nhất cho tôi.
                """.formatted(targetPlan.getDayOfWeek().name(), serializeHistory(history), serializeChat(pastChats));

        // 6. Gọi Gemini
        String aiResponse = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
        // 8. Lưu lịch sử chat
        return saveChatLog(user, userPrompt, systemPrompt, aiResponse);
    }

    public Menu mapToEntity(AiMenuSuggestionResponse dto, MenuPlan plan) {
        Menu menu = Menu.builder()
                .menuPlan(plan)
                .notes(dto.getNotes())
                .status(Menu.MenuStatus.IN_PROGRESS)
                .actualTotalCalories(dto.getTotalCalories())
                .actualTotalProtein(dto.getTotalProtein())
                .actualTotalCarb(dto.getTotalCarbs())
                .actualTotalFat(dto.getTotalFat())
                .meals(new ArrayList<>())
                .build();

        dto.getMeals().forEach(mealDto -> {
            Meal meal = Meal.builder()
                    .mealType(Meal.MealType.valueOf(mealDto.getMealType()))
                    .menu(menu)
                    .mealRecipe(new ArrayList<>())
                    .build();

            mealDto.getRecipes().forEach(mrDto -> {
                Recipe recipe = recipeRepository.findById(mrDto.getRecipeId()).orElse(null);
                if (recipe != null) {
                    MealRecipe mr = MealRecipe.builder()
                            .recipe(recipe)
                            .meal(meal)
                            .calories(recipe.getCalories())
                            .protein(recipe.getProtein())
                            .carbs(recipe.getCarbs())
                            .fat(recipe.getFat())
                            .mealRecipeIngredients(new ArrayList<>())
                            .build();

                    // Thêm logic mapping ingredients nếu AI trả về chi tiết quantity
                    recipe.getRecipeIngredients().forEach(
                            recipeIngredient -> {
                                MealRecipeIngredient mri = MealRecipeIngredient.builder()
                                        .ingredient(recipeIngredient.getIngredient())
                                        .mealRecipe(mr)
                                        .quantity(recipeIngredient.getQuantity())
                                        .build();
                                mr.getMealRecipeIngredients().add(mri);
                            }
                    );
                    meal.getMealRecipe().add(mr);
                }
            });
            menu.getMeals().add(meal);
        });
        return menu;
    }

    private ChatHistory saveChatLog(User user, String prompt, String systemPrompt, String response) {
        ChatHistory log = ChatHistory.builder()
                .user(user)
                .userPrompt(prompt)
                .systemPrompt(systemPrompt)
                .aiResponse(response)
                .category(ChatHistory.ChatCategory.MENU_GENERATION)
                .build();
        return chatHistoryRepository.save(log);
    }

    private String serializeHistory(List<Menu> history) {
        return history.stream().map(m -> m.getNotes()).collect(Collectors.joining(", "));
    }

    private String serializeChat(List<ChatHistory> chats) {
        return chats.stream().map(c -> c.getUserPrompt()).collect(Collectors.joining(" | "));
    }

    public Menu saveMenuFromChat(Long chatId){
        com.health_fitness.model.nutrition.ChatHistory chatHistory = chatHistoryRepository.findById(chatId).orElseThrow(()->new NotFoundException("Not found"));
        AiMenuSuggestionResponse aiResult = converter.convert(chatHistory.getAiResponse());
        Menu menuToDay = this.menuService.getMenuToDay();
        if(menuToDay!=null) this.menuRepository.delete(menuToDay);
        Menu menu = mapToEntity(aiResult, this.menuPlanService.getMenuPlanToday());
        return this.menuRepository.save(menu);
    }

    public Menu getMenuFromChat(Long chatId){
        com.health_fitness.model.nutrition.ChatHistory chatHistory = chatHistoryRepository.findById(chatId).orElseThrow(()->new NotFoundException("Not found"));
        AiMenuSuggestionResponse aiResult = converter.convert(chatHistory.getAiResponse());
        return mapToEntity(aiResult, this.menuPlanService.getMenuPlanToday());
    }
}