package pptik.startup.ghvmobile.Support;

import java.io.Serializable;

/**
 * Created by edo on 6/12/2016.
 */
public class Program implements Serializable {
    private String _namaprogram, _lokasiprogram, _mulai, _akhir,_supervisor,_deskripsi,_keterangan,_latitude,_longitude,_pathfoto;
    private int _idprogram,_status;

    public String getPathfoto() {
        return _pathfoto;
    }
    public void setPathfoto(String _pathfoto) {
        this._pathfoto = _pathfoto;
    }


    public String getLongitude() {
        return _longitude;
    }
    public void setLongitude(String _longitude) {
        this._longitude = _longitude;
    }

    public String getLatitude() {
        return _latitude;
    }
    public void setLatitude(String _latitude) {
        this._latitude = _latitude;
    }

    public String getKeterangan() {
        return _keterangan;
    }
    public void setKeterangan(String _keterangan) {
        this._keterangan = _keterangan;
    }

    public String getDeskripsi() {
        return _deskripsi;
    }
    public void setDeskripsi(String _deskripsi) {
        this._deskripsi = _deskripsi;
    }

    public String getSupervisor() {
        return _supervisor;
    }
    public void setSupervisor(String _supervisor) {
        this._supervisor = _supervisor;
    }


    public String getAkhir() {
        return _akhir;
    }
    public void setAkhir(String _akhir) {
        this._akhir = _akhir;
    }

    public String getMulai() {
        return _mulai;
    }
    public void setMulai(String _mulai) {
        this._mulai = _mulai;
    }


    public int getIdProgram() {
        return _idprogram;
    }
    public void setIdProgram(int _idprogram) {
        this._idprogram = _idprogram;
    }


    public int getStatus() {
        return _status;
    }
    public void setStatus(int _status) {
        this._status = _status;
    }


    public String getNamaProgram() {
        return _namaprogram;
    }
    public void setNamaProgram(String _namaprogram) {
        this._namaprogram = _namaprogram;
    }

    public String getLokasiProgram() {
        return _lokasiprogram;
    }
    public void setLokasiProgram(String _lokasiprogram) {
        this._lokasiprogram = _lokasiprogram;
    }

}
