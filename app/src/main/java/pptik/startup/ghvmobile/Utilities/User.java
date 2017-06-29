
package pptik.startup.ghvmobile.Utilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id_user")
    @Expose
    public Integer idUser;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("path_image")
    @Expose
    public String pathImage;
    @SerializedName("lokasi")
    @Expose
    public String lokasi;
    @SerializedName("role_id")
    @Expose
    public Integer roleId;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

}
