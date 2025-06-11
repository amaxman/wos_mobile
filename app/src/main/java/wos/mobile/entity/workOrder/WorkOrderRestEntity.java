package wos.mobile.entity.workOrder;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

import wos.mobile.entity.BasicRestEntity;

public class WorkOrderRestEntity extends BasicRestEntity {
    private String title;
    private String content;
    @JSONField(name = "start_time")
    private Date starTime;
    @JSONField(name = "end_time")
    private Date endTime;

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

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
