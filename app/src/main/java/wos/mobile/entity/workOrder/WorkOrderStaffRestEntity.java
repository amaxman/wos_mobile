package wos.mobile.entity.workOrder;

import java.util.Date;

import wos.mobile.entity.BasicRestEntity;

public class WorkOrderStaffRestEntity extends BasicRestEntity {
    private String woId;        // 工单标识
    private String staffId;        // 执行人标识
    private String staffName;        // 执行人姓名
    private Integer workProgress;        // 进度

    private String title;
    private String content;
    private Date startTime;
    private Date endTime;
    private String cateCode;        // 类别
    private String cateName;        // 类别
    private String levelCode;        // 级别
    private String levelName;        // 级别

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Integer getWorkProgress() {
        return workProgress;
    }

    public void setWorkProgress(Integer workProgress) {
        this.workProgress = workProgress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCateCode() {
        return cateCode;
    }

    public void setCateCode(String cateCode) {
        this.cateCode = cateCode;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
