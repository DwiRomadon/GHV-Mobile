package pptik.startup.ghvmobile.Support;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by edo on 8/6/2016.
 */
public class PhotoManager {
    protected List<String>fileList;
    protected String sourceFile;
    protected String sourceDest;
    protected List<String> fileLisZip;

    public PhotoManager (){
        fileList = new ArrayList<>();
        fileLisZip = new ArrayList<>();
    }

    public File[] getPhotoFromGallery(String path){
        File fileSource = new File(path);
        if(fileSource.isDirectory()){
            File[] listFile = fileSource.listFiles();
            return listFile;
        }else{
            File listFile[]={fileSource};
            return listFile;
        }
    }

    public boolean deletePhotoOrSingleFile(String path){
        File fileSource = new File(path);
        if(fileSource.isDirectory()){
            File[] listFile = fileSource.listFiles();
            int counterFileDelete =0;
            for(File file:listFile){
                if(file.delete()){counterFileDelete++;}
            }

            if(counterFileDelete== listFile.length){
                return true;
            } else {
                return false;
            }
        }else{
            return fileSource.delete();
        }
    }

    public void setSourceZipDest() {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(getPathAppsDirectory() + File.separator + "photos.zip")));
            generatezipPhotoFile(zos);

            Log.d("Status Generate File ", "Success");
        }catch(Exception e){
            Log.d("Error Generate Zip", e.getMessage());
        }
    }



    protected void generatezipPhotoFile(ZipOutputStream zos)throws Exception {

        /*ZipFile zip = new ZipFile(getPathAppsDirectory()+File.separator+"data.zip");
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        zip.addFolder(getRootPhotoDirectory(), parameters);*/

        byte[] readBuffer = new byte[1024];

        try{

            for(String file : this.fileLisZip){

                ZipEntry ze= new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(getRootPhotoDirectory() + File.separator + file);

                int len;
                while ((len = in.read(readBuffer)) > 0) {
                    zos.write(readBuffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();

            //remember close it
            zos.finish();
            zos.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private String generateZipEntry(String file){
        String result = file.substring(getRootPhotoDirectory().length()+1);
        String[] folderSp = result.split(File.separator);
        Log.d("ZIP FILE", "generateZipEntry: " + folderSp[0].toString());
        return file.substring(getRootPhotoDirectory().length()+1, file.length());
    }

    public void generateFileList(File node, String noPengaduan){
        //add file only
        if(node.isFile()){
            this.fileLisZip.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if(node.isDirectory()){
            String[] subNote = node.list();
            for(String filename : subNote){
                Log.d("ZIP FOLDER", "generateFileList: "+filename);
                generateFileList(new File(node, filename), noPengaduan);
            }
        }
    }

    public boolean checkPhotoDirectory(String path){
        if(new File(path).exists()){
            return true;
        }else{
            return false;
        }
    }

    public String getNewPathAppsDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public String getPathAppsDirectory(){
        return Environment.getExternalStorageDirectory()+File.separator+"pdam";
    }

    public String getRootPhotoDirectory(){
        return getPathAppsDirectory()+File.separator+"photos";
    }

    public String getRootDCIMDirectory(){
        return Environment.getExternalStorageDirectory()+File.separator+"DCIM";
    }

    public String getPhotoDirectoryFromID(String idKeluhan){
        return getRootPhotoDirectory()+File.separator+idKeluhan;
    }


    public boolean createPathApps(){
        File fileApps = new File(getRootPhotoDirectory());
        if(fileApps.exists()){
            return false;
        }else{
            return fileApps.mkdir();
        }
    }

    public boolean copyPhotoFromGallery(File sourceFile, File sourceDestination) {
        InputStream input =null;
        OutputStream output=null;
        try{

            /*
            input = new FileInputStream(sourceFile);
            output = new FileOutputStream(sourceDestination);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
            input.close();
            output.close();
            */


            FileOutputStream newFos = new FileOutputStream(sourceDestination);
            Bitmap bmp = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
            //Bitmap bmpTemp = bmp.createScaledBitmap(bmp, 400,400, true);
            //Bitmap bmpTemp = ImageScaleUtil.createScaledBitmap(bmp, 400, 400, ImageScaleUtil.ScalingLogic.FIT);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, newFos);

            newFos.flush();
            newFos.close();

            //bmpTemp.recycle();

            return true;

        }catch (Exception e){
            return false;
        }
    }
}