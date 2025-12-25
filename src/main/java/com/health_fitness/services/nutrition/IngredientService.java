package com.health_fitness.services.nutrition;

import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.nutrition.Ingredient;
import com.health_fitness.repository.nutrition.IngredientRepository;
import com.health_fitness.utils.ImageUtils;
import com.health_fitness.utils.ImageUtils.ImageType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ImageUtils imageUtils;


    public IngredientService(IngredientRepository ingredientRepo, ImageUtils imageUtils) {
        this.ingredientRepository = ingredientRepo;
        this.imageUtils = imageUtils;
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient createIngredient(Ingredient ingredient) throws IOException {
        MultipartFile file = ingredient.getImage();
        if (file != null && !file.isEmpty()) {
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.INGREDIENT);
            ingredient.setImageId((String) uploadResult.get(0));
            ingredient.setImageUrl((String) uploadResult.get(1));
        }
        ingredient.setId(null);
        return ingredientRepository.save(ingredient);
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient updateIngredient(Integer id, Ingredient ingredient) throws IOException {
        Ingredient ingredientToSave = getIngredient(id);
        BeanUtils.copyProperties(ingredient, ingredientToSave, "id");
        MultipartFile file = ingredient.getImage();
        if (file != null && !file.isEmpty()) {
            if (ingredient.getImageId() != null) {
                imageUtils.deleteImage(ingredient.getImageId());
            }
            List<Object> uploadResult = imageUtils.uploadImage(file, ImageType.INGREDIENT);
            ingredient.setImageId((String) uploadResult.get(0));
            ingredient.setImageUrl((String) uploadResult.get(1));
        }

        return ingredientRepository.save(ingredient);
    }

    @PreAuthorize("isAuthenticated()")
    public Ingredient getIngredient(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    @PreAuthorize("isAuthenticated()")
    public List<Ingredient> getIngredientById(List<Integer> ids) {
        return ingredientRepository.findAllById(ids);
    }


    public Page<Ingredient> getAllIngredients(final String searchString, Pageable pageable) {
        Specification<Ingredient> specification = new Specification<Ingredient>() {
            @Override
            public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(searchString!=null){
                    return criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), "%"+searchString+"%"),
                            criteriaBuilder.like(root.get("description"), "%"+searchString+"%")
                            );
                }
                return null;
            }
        };
        return ingredientRepository.findAll(specification, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteIngredient(Integer id) throws IOException {
        Ingredient ingredient = this.getIngredient(id);
        if (ingredient.getImageId() != null) {
            imageUtils.deleteImage(ingredient.getImageId());
        }
        ingredientRepository.deleteById(id);
    }
    @PreAuthorize("isAuthenticated()")
    public List<Ingredient> importIngredientsFromExcel(MultipartFile file, List<MultipartFile> images) throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Kiểm tra định dạng file
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("File must be Excel format (.xlsx or .xls)");
        }

        // Tạo map để map tên file với MultipartFile
        Map<String, MultipartFile> imageMap = new HashMap<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageName = image.getOriginalFilename();
                if (imageName != null && !imageName.isEmpty()) {
                    imageMap.put(imageName.toLowerCase(), image);
                }
            }
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheet("Ingredients");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;

                // Bỏ qua dòng trống
                if (isRowEmpty(row)) {
                    continue;
                }

                try {
                    Ingredient ingredient = new Ingredient();

                    // Name (required)
                    String name = getCellValueAsString(row.getCell(0));
                    if (name == null || name.trim().isEmpty()) {
                        errors.add("Row " + rowNumber + ": Name is required");
                        continue;
                    }
                    ingredient.setName(name.trim());

                    // Description
                    ingredient.setDescription(getCellValueAsString(row.getCell(1)));

                    // Calories
                    Float calories = getCellValueAsFloat(row.getCell(2));
                    if (calories != null && calories < 0) {
                        errors.add("Row " + rowNumber + ": Calories must be >= 0");
                        continue;
                    }
                    ingredient.setCalories(calories);

                    // Protein
                    Float protein = getCellValueAsFloat(row.getCell(3));
                    if (protein != null && protein < 0) {
                        errors.add("Row " + rowNumber + ": Protein must be >= 0");
                        continue;
                    }
                    ingredient.setProtein(protein);

                    // Carbs
                    Float carbs = getCellValueAsFloat(row.getCell(4));
                    if (carbs != null && carbs < 0) {
                        errors.add("Row " + rowNumber + ": Carbs must be >= 0");
                        continue;
                    }
                    ingredient.setCarbs(carbs);

                    // Fat
                    Float fat = getCellValueAsFloat(row.getCell(5));
                    if (fat != null && fat < 0) {
                        errors.add("Row " + rowNumber + ": Fat must be >= 0");
                        continue;
                    }
                    ingredient.setFat(fat);

                    // Image File Name (column 6)
                    String imageFileName = getCellValueAsString(row.getCell(6));
                    if (imageFileName != null && !imageFileName.trim().isEmpty()) {
                        String imageKey = imageFileName.trim().toLowerCase();
                        MultipartFile imageFile = imageMap.get(imageKey);

                        if (imageFile != null && !imageFile.isEmpty()) {
                            try {
                                List<Object> uploadResult = imageUtils.uploadImage(imageFile, ImageType.INGREDIENT);
                                ingredient.setImageId((String) uploadResult.get(0));
                                ingredient.setImageUrl((String) uploadResult.get(1));
                            } catch (Exception e) {
                                errors.add("Row " + rowNumber + ": Failed to upload image '" + imageFileName + "' - " + e.getMessage());
                                continue;
                            }
                        } else {
                            errors.add("Row " + rowNumber + ": Image file '" + imageFileName + "' not found in uploaded images");
                            continue;
                        }
                    }

                    // Lưu ingredient
                    Ingredient savedIngredient = ingredientRepository.save(ingredient);
                    ingredients.add(savedIngredient);

                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": " + e.getMessage());
                }
            }

            // Nếu có lỗi, throw exception với danh sách lỗi
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Import completed with errors: " + String.join("; ", errors));
            }

        } catch (Exception e) {
            throw new IOException("Error processing Excel file: " + e.getMessage(), e);
        }

        return ingredients;
    }

    // Cập nhật generateExcelTemplate để thêm cột Image File Name
    public byte[] generateExcelTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ingredients");

            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Tạo header row với cột Image File Name
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Name*", "Description", "Calories", "Protein (g)", "Carbs (g)", "Fat (g)", "Image File Name"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // Thêm 1 dòng mẫu
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("Chicken Breast");
            exampleRow.createCell(1).setCellValue("Lean protein source");
            exampleRow.createCell(2).setCellValue(165);
            exampleRow.createCell(3).setCellValue(31);
            exampleRow.createCell(4).setCellValue(0);
            exampleRow.createCell(5).setCellValue(3.6);
            exampleRow.createCell(6).setCellValue("chicken.jpg");

            // Thêm sheet hướng dẫn
            Sheet instructionSheet = workbook.createSheet("Instructions");
            Row instruction1 = instructionSheet.createRow(0);
            instruction1.createCell(0).setCellValue("HƯỚNG DẪN SỬ DỤNG TEMPLATE");

            Row instruction2 = instructionSheet.createRow(2);
            instruction2.createCell(0).setCellValue("1. Các trường có dấu * là bắt buộc");

            Row instruction3 = instructionSheet.createRow(3);
            instruction3.createCell(0).setCellValue("2. Calories, Protein, Carbs, Fat phải là số >= 0");

            Row instruction4 = instructionSheet.createRow(4);
            instruction4.createCell(0).setCellValue("3. Không xóa dòng header");

            Row instruction5 = instructionSheet.createRow(5);
            instruction5.createCell(0).setCellValue("4. Bắt đầu nhập dữ liệu từ dòng 2");

            Row instruction6 = instructionSheet.createRow(6);
            instruction6.createCell(0).setCellValue("5. Cột 'Image File Name': Nhập tên file ảnh (VD: chicken.jpg)");

            Row instruction7 = instructionSheet.createRow(7);
            instruction7.createCell(0).setCellValue("6. Upload các file ảnh kèm theo khi import Excel");

            Row instruction8 = instructionSheet.createRow(8);
            instruction8.createCell(0).setCellValue("7. Tên file trong Excel phải TRÙNG KHỚP với tên file ảnh upload");

            instructionSheet.setColumnWidth(0, 60 * 256);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Helper methods
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Float getCellValueAsFloat(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (float) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? null : Float.parseFloat(value);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
