import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

public class ExcelReader
{
    private Workbook workbook;
    private Sheet sheet;
    public ExcelReader() throws Exception
    {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        File selectedFile = null;
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = jfc.getSelectedFile();
        }
        FileInputStream excelFile = new FileInputStream(selectedFile);
        this.workbook = new XSSFWorkbook(excelFile);
        this.sheet = workbook.getSheetAt(0);
    }
    public static HashMap<String, String> getCompounds() throws Exception
    {
        FileInputStream excelFile = new FileInputStream(new File("C:\\Users\\speri1\\Documents\\Data\\Need Cas Number.xlsx"));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 2; i <= 28162; i ++)
        {
            CellReference reference = new CellReference("B" + i);
            Row row = datatypeSheet.getRow(reference.getRow());
            Cell cell = row.getCell(reference.getCol());
            map.put(cell.getStringCellValue(), "B" + i);
        }
        return map;
    }
    public void writeToCell(String cell, String value) throws Exception
    {
        CellReference reference = new CellReference(cell);
        Row row = sheet.getRow(reference.getRow());
        Cell position = row.getCell(reference.getCol());
        position.setCellType(Cell.CELL_TYPE_STRING);
        position.setCellValue(value);
    }
    public void save() throws Exception
    {
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\speri1\\Documents\\Data\\Need Cas Number.xlsx"));
        workbook.write(fos);
        fos.close();
    }
}
