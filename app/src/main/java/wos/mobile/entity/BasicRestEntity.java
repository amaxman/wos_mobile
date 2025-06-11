package wos.mobile.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Rest基类
 */
public abstract class BasicRestEntity {
    private String id;
    @JSONField(name = "create_by")
    private String createBy;
    @JSONField(name = "create_time")
    private Date createTime;
    @JSONField(name = "update_by")
    private Date updateBy;
    @JSONField(name = "update_time")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Date updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
