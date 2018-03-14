package com.tanim.amarekattor.database;

import android.os.AsyncTask;

/**
 * Created by tanim on 3/11/2018.
 */

public class InsertMovie extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        /*VideoVideoModel model = new VideoVideoModel();
        try {
            InputStream inputStream = null;
            inputStream = App.getContext().getAssets().open("documentary.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            int i = 0;
            Iterator rows = sheet.rowIterator();

            while (rows.hasNext()) {
                row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                String name = null, link;
                while (cells.hasNext()) {
                    cell = (XSSFCell) cells.next();
                    if (i > 1 && cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                        if (i % 2 == 0) {
                            name = cell.getStringCellValue();
                        } else {
                            link = cell.getStringCellValue();
                            VideoEntity entity = new VideoEntity();
                            link = link.replace("https://www.youtube.com/watch?v=", "");
                            //UUID uuid = UUID.randomUUID();
                            entity.id = link;
                            entity.name = name;
                            entity.type = Constant.MOVIE;
                            entity.time = "10:10";
                            //Log.d("Check",link);
                            VideoEntity existEntity = model.getData(entity.id);
                            //if()
                            if (existEntity == null) {
                                model.insert(entity);
                            } else {
                                if (!(existEntity.id.equals(entity.id) && existEntity.name.equals(entity.name)
                                        && entity.type.equals(existEntity.type) && entity.time.equals(existEntity.time))) {


                                    model.insert(entity);
                                }
                            }


                            //list.add(new HomeActivity.VideoEntry(name, link));
                        }
                        //System.out.print(cell.getStringCellValue()+" ");
                    } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                        //Log.d("Check",cell.getNumericCellValue()+" ");
                        //System.out.print(cell.getNumericCellValue()+" ");
                    } else {
                        //U Can Handel Boolean, Formula, Errors
                    }
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }
}
