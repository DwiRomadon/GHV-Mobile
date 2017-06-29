package pptik.startup.ghvmobile.Support;
import java.io.Serializable;
/**
 * Created by GIGABYTE on 16/06/2016.
 */
public class DataProgram implements Serializable {
    private String _nama_program, _lokasi_program;
    private int _id_program;
    private String _tanggal,_supervisor,_pathfoto;

    public String get_pathfoto() {
        return _pathfoto;
    }
    public void set_pathfoto(String _pathfoto) {
        this._pathfoto = _pathfoto;
    }


    public String get_supervisor() {
        return _supervisor;
    }
    public void set_supervisor(String _supervisor) {
        this._supervisor = _supervisor;
    }


    public String get_tanggal() {
        return _tanggal;
    }
    public void set_tanggal(String _tanggal) {
        this._tanggal = _tanggal;
    }


    public String getNamaProgram() {
        return _nama_program;
    }
    public void setNamaProgram(String _nama_program) {
        this._nama_program = _nama_program;
    }

    public String getLokasiProgram() {
        return _lokasi_program;
    }
    public void setLokasiProgram(String _lokasi_program) {
        this._lokasi_program = _lokasi_program;
    }

    public int getIdProgram() {
        return _id_program;
    }
    public void setIdProgram(int _id_program) {
        this._id_program = _id_program;
    }
}
