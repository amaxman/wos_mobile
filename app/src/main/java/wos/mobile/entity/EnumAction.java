package wos.mobile.entity;

public enum EnumAction {
    login("登陆")
    ,login_ui("登陆UI响应")
    ,add("增加")
    ,add_ui("增加UI响应")
    ,add_activity("增加启动界面")
    ,delete("删除")
    ,delete_ui("删除UI响应")
    ,edit("修改")
    ,edit_ui("修改UI响应")
    ,edit_activity("修改启动界面")
    ,query("查询")
    ,query_ui("查询UI响应")
    ,detail_activity("明细列表界面")
    ,toast("吐司显示")
    ;

    private String name;

    EnumAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 通过value取描述
     * @param value
     * @return
     */
    public static EnumAction getByOrdinal(int value) {
        for (EnumAction enums : EnumAction.values()) {
            if (enums.ordinal()== value) return enums;
        }
        return null;
    }
}
