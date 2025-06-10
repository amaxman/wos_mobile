package wos.mobile.entity.auth;


// 权限
public class PermissionRestEntity {
	private String funcCode = ""; // 功能编码
	private String title = ""; //标题

	private String imageUrl; //远程图片路径

	private Integer orderNum; //排序

	public PermissionRestEntity() {

	}



	public PermissionRestEntity(String funcCode, String title, String imageUrl) {
		this.funcCode = funcCode;
		this.title = title;
		this.imageUrl=imageUrl;
	}

	public String getFuncCode() {
		return funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
}
