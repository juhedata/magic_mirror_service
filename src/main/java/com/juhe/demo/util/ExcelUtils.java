package com.juhe.demo.util;

import com.juhe.demo.ao.PersonalAO;
import com.juhe.demo.constant.SystemConstant;
import com.juhe.demo.constant.SystemConstant.Common;
import com.juhe.demo.constant.SystemConstant.DateFormatInstant;
import com.juhe.demo.constant.SystemConstant.Personal;
import com.juhe.demo.entity.PersonalIcon;
import com.juhe.demo.entity.PersonalMac;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.springframework.web.multipart.MultipartFile;

/**
 * @CLassName ExcelUtils
 * @Description Excel处理工具
 * @Author xxmfypp
 * @Date 2019/7/11 14:19
 * @Version 1.0
 **/
@Slf4j
public class ExcelUtils {

    /**
     * @return java.util.List<com.juhe.demo.ao.PersonalAO>
     * @Author xxmfypp
     * @Description 获取人员信息并保存人员照片到指定位置
     * @Date 15:41 2019/7/11
     * @Param [file, fileType, savePath]
     **/
    public static List<PersonalAO> getPersonalInfoFromExcel(MultipartFile file, String fileType, String savePath)
        throws IOException {
        //读取sheet 0
        Sheet sheet;
        Map<String, List<PictureData>> mapPicture;
        if ("xls".equals(fileType)) {
            //文件流对象
            Workbook wb = new HSSFWorkbook(file.getInputStream());
            sheet = wb.getSheetAt(0);
            mapPicture = getPicturesXLS((HSSFSheet) sheet);
        } else {
            Workbook wb = new XSSFWorkbook(file.getInputStream());
            sheet = wb.getSheetAt(0);
            mapPicture = getPicturesXLSX((XSSFSheet) sheet);
        }
        List<PersonalAO> personalAOList = getPersonalAOFromSheet(savePath, sheet, mapPicture);
        return personalAOList;
    }

    private static List<PersonalAO> getPersonalAOFromSheet(String savePath, Sheet sheet,
        Map<String, List<PictureData>> mapPicture) {
        List<PersonalAO> personalAOList = new ArrayList<>();
        //第一行是列名，所以不读
        int firstRowIndex = sheet.getFirstRowNum() + 1;
        int lastRowIndex = sheet.getLastRowNum();
        PersonalAO personalAO;
        List<byte[]> pictures = new ArrayList<>();
        List<String> pictureNames = new ArrayList<>();
        //遍历行
        for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
            personalAO = new PersonalAO();
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                //遍历列
                for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                    Cell cell = row.getCell(cIndex);
                    if (cell != null && StringUtils.isNotBlank(cell.toString())) {
                        switch (cIndex) {
                            case 0:
                                personalAO.setName(cell.toString().trim());
                                break;
                            case 1:
                                personalAO.setGender("男".equals(String.valueOf(cell).trim()) ? 0 : 1);
                                break;
                            case 2:
                                personalAO
                                    .setBirthday(DateFormatInstant.simpleDateFormatter.format(cell.getDateCellValue()));
                                break;
                            case 3:
                                personalAO.setGroupName(String.valueOf(cell).trim());
                                break;
                            case 4:
                                personalAO.setEmployeeNo(String.valueOf(cell).trim());
                                break;
                            case 5:
                                personalAO.setTitle(String.valueOf(cell).trim());
                                break;
                            case 6:
                                personalAO.setDepartment(String.valueOf(cell).trim());
                                break;
                            case 7:
                                personalAO.setCompany(String.valueOf(cell).trim());
                                break;
                            case 8:
                                personalAO.setMac(String.valueOf(cell).trim());
                                break;
                            case 9:
                                personalAO.setNote(String.valueOf(cell).trim());
                                break;
                            default:
                                break;
                        }
                    }
                }
                List<PictureData> pictureData = mapPicture.get(String.valueOf(rIndex));
                if (pictureData != null && !pictureData.isEmpty()) {
                    List<PersonalIcon> personalIcons = new ArrayList<>();
                    String imageName;
                    String ext;
                    PersonalIcon personalIcon;
                    for (PictureData data : pictureData) {
                        pictures.add(data.getData());
                        ext = data.suggestFileExtension();
                        imageName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
                        pictureNames.add(imageName);
                        personalIcon = new PersonalIcon(imageName, null, null);
                        personalIcons.add(personalIcon);
                    }
                    personalAO.setIcon(personalIcons.get(0).getIcon());
                    personalAO.setIcons(personalIcons);
                } else {
                    personalAO.setIcon("");
                    personalAO.setIcons(new ArrayList<>());
                }
                String mac = personalAO.getMac();
                if (StringUtils.isNotBlank(mac)) {
                    List<PersonalMac> macs = Arrays.stream(mac.split(Common.DELIMITER))
                        .map(val -> new PersonalMac(val.toUpperCase(), null)).collect(Collectors.toList());
                    personalAO.setMacs(macs);
                }
                personalAO.setDeleted(SystemConstant.Personal.UNDELETE);
                personalAOList.add(personalAO);
            }
        }
        printImg(pictures, pictureNames, savePath);
        return personalAOList;
    }

    /**
     * @Author xxmfypp
     * @Description // 保存图片
     * @Date 15:19 2019/7/11
     * @Param [pictureData:图片,savePath保存路径]
     **/
    public static void printImg(List<byte[]> pictures, List<String> pictureNames, String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        IntStream.range(0, pictures.size()).parallel().forEach(i -> {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
                pictures.get(i)); ByteArrayInputStream suitStream = new ByteArrayInputStream(pictures.get(i));) {
                String pictureName = pictureNames.get(i);
                Thumbnails.of(inputStream).scale(Personal.IMAGE_SCALE).outputQuality(Personal.IMAGE_QUALITY)
                    .toFile(FileUtil.getCompressImageName(savePath, pictureName));
                Thumbnails.of(suitStream).scale(Personal.IMAGE_SCALE).outputQuality(1d)
                    .toFile(savePath + File.separator + pictureName);
            } catch (IOException e) {
                log.error("save person image failed," + e.getMessage());
            }
        });
    }

    /**
     * 获取图片和位置 (xls)
     */
    public static Map<String, List<PictureData>> getPicturesXLS(HSSFSheet sheet) {
        Map<String, List<PictureData>> map = new HashMap<>(8);
        List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
        for (HSSFShape shape : list) {
            if (shape instanceof HSSFPicture) {
                HSSFPicture picture = (HSSFPicture) shape;
                HSSFClientAnchor cAnchor = (HSSFClientAnchor) picture.getAnchor();
                PictureData pdata = picture.getPictureData();
                // 行号-列号
                String key = String.valueOf(cAnchor.getRow1());
                if (map.containsKey(key)) {
                    map.get(key).add(pdata);
                } else {
                    List<PictureData> dataList = new ArrayList<>(Arrays.asList(pdata));
                    map.put(key, dataList);
                }
            }
        }
        return map;
    }

    /**
     * 获取图片和位置 (xlsx)
     */
    public static Map<String, List<PictureData>> getPicturesXLSX(XSSFSheet sheet) {
        Map<String, List<PictureData>> map = new HashMap<>(8);
        List<POIXMLDocumentPart> list = sheet.getRelations();
        for (POIXMLDocumentPart part : list) {
            if (part instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) part;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture picture = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = picture.getPreferredSize();
                    CTMarker marker = anchor.getFrom();
                    String key = String.valueOf(marker.getRow());
                    if (map.containsKey(key)) {
                        map.get(key).add(picture.getPictureData());
                    } else {
                        List<PictureData> dataList = new ArrayList<>(Arrays.asList(picture.getPictureData()));
                        map.put(key, dataList);
                    }

                }
            }
        }
        return map;
    }

}
