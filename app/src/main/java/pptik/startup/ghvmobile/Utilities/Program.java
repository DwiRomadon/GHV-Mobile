package pptik.startup.ghvmobile.Utilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fiyyanp on 5/9/2016.
 */
public class Program {
    @SerializedName("id_program")
    @Expose
    public Integer _idProgram;

    @SerializedName("nama_program")
    @Expose
    public String _namaProgram;

    @SerializedName("lokasi_program")
    @Expose
    public String _lokasiProgram;

    @SerializedName("mulai")
    @Expose
    public String _muLai;

    @SerializedName("akhir")
    @Expose
    public String _akHir;

    @SerializedName("supervisor")
    @Expose
    public String _superVisor;

    @SerializedName("deskripsi")
    @Expose
    public String _desKripsi;

    @SerializedName("keterangan")
    @Expose
    public String _keteRangan;

    @SerializedName("latitude")
    @Expose
    public String _latitude;

    @SerializedName("longitude")
    @Expose
    public String _longitude;

    @SerializedName("status")
    @Expose
    public Integer _status;

}
