package pptik.startup.ghvmobile.Utilities;

import java.io.Serializable;

/**
 * Created by GIGABYTE on 06/10/2016.
 */

public class Image implements Serializable {
    private int _idimage,_idprogram,_iduser;
    private String _namaimage,_pathimage,_createdat,_namauser;

    public String get_namauser() {
        return _namauser;
    }
    public void set_namauser(String _namauser) {
        this._namauser = _namauser;
    }

    public String get_createdat() {
        return _createdat;
    }
    public void set_createdat(String _createdat) {
        this._createdat = _createdat;
    }


    public String get_namaimage() {
        return _namaimage;
    }
    public void set_namaimage(String _namaimage) {
        this._namaimage = _namaimage;
    }


    public String get_pathimage() {
        return _pathimage;
    }
    public void set_pathimage(String _pathimage) {
        this._pathimage = _pathimage;
    }

    public int get_iduser() {
        return _iduser;
    }
    public void set_iduser(int _iduser) {
        this._iduser = _iduser;
    }


    public int get_idprogram() {
        return _idprogram;
    }
    public void set_idprogram(int _idprogram) {
        this._idprogram = _idprogram;
    }


    public int get_idimage() {
        return _idimage;
    }
    public void set_idimage(int _idimage) {
        this._idimage = _idimage;
    }



}
