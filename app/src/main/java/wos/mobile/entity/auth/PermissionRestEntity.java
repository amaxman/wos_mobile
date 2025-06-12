package wos.mobile.entity.auth;


// 权限
public class PermissionRestEntity {
	private String code = ""; // 功能编码
	private String title = ""; //标题

	private String imagePath; //远程图片路径

	public PermissionRestEntity() {

	}

	public PermissionRestEntity(String code, String title, String imagePath) {
		this.code = code;
		this.title = title;
		this.imagePath = imagePath;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
