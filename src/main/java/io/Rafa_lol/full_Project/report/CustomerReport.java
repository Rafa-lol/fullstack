package io.Rafa_lol.full_Project.report;


import io.Rafa_lol.full_Project.domain.Customer;
import io.Rafa_lol.full_Project.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.cfg.defs.EANDef;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

@Slf4j
public class CustomerReport {


    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customers;
    private static String[] HEADERS = { "ID", "Name", "Email", "Type", "Status", "Address", "Phone", "Created At"};


    public CustomerReport(List<Customer> customers) {
        this.customers = customers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Customers");
    }

    private void createSheet(){
        Row headerRow = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        range(0, HEADERS.length).forEach(index ->{
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(HEADERS[index]);
            cell.setCellStyle(style);
        });
    }

    public InputStreamResource export(){
        return generateReport();
    }



    private InputStreamResource generateReport(){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontHeight(10);
            style.setFont(font);
            int rowIndex = 1;
            for(Customer customer : customers){
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getType());
                row.createCell(4).setCellValue(customer.getStatus());
                row.createCell(5).setCellValue(customer.getAddress());
                row.createCell(6).setCellValue(customer.getPhone());
                row.createCell(7).setCellValue(customer.getCreatedAt());
            }
            workbook.write(out);
            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("Unable to export report file");
        }
    }

}
