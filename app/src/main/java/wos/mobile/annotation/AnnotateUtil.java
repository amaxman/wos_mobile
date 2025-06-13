package wos.mobile.annotation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Annotation
 * @author lushiju
 *
 */
public class AnnotateUtil {

    public static void initBindView(Object currentClass, View sourceView,String packageName) {
        Field[] fieldsCurrent = currentClass.getClass().getDeclaredFields();
        Field[] fieldsParent = currentClass.getClass().getSuperclass().getDeclaredFields();
        List<Field> fieldList=new ArrayList<>();
        if (fieldsCurrent.length>0) fieldList.addAll(Arrays.asList(fieldsCurrent));
        if (fieldsParent.length>0) fieldList.addAll(Arrays.asList(fieldsParent));
        if(!fieldList.isEmpty()) {

            for(int index = 0; index < fieldList.size(); ++index) {
                Field field = fieldList.get(index);
                BindView bindView = (BindView)field.getAnnotation(BindView.class);
                if(bindView != null) {
                    String idName = bindView.id();
                    int viewId = sourceView.getResources().getIdentifier(idName, "id", packageName);
                    boolean clickListener = bindView.click();

                    try {
                        field.setAccessible(true);
                        if(clickListener) {
                            sourceView.findViewById(viewId).setOnClickListener((View.OnClickListener)currentClass);
                        }
                        field.set(currentClass, sourceView.findViewById(viewId));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        System.out.print("=======");

    }

    //#region Activity
    public static void initBindView(Activity aty) {
        initBindView(aty, aty.getWindow().getDecorView(),aty.getPackageName());
    }

    public static void initBindView(View view) {
        Context cxt = view.getContext();
        if(cxt instanceof Activity) {
            initBindView((Activity)cxt);
        } else {
            Log.d("AnnotateUtil.java", "the view do not have root view");
        }
    }
    //#endregion

    //#region Fragment
    public static void initBindView(Fragment frag) {
        initBindView(frag, frag.getActivity().getWindow().getDecorView(),frag.getActivity().getPackageName());
    }

    public static void initBindView(Fragment fragment, View view) {
        initBindView(fragment, view, fragment.requireContext().getPackageName());
    }
    //#endregion

    //#region Fragment
    public static void initBindView(DialogFragment fragment, View view) {
        initBindView(fragment, view, fragment.requireContext().getPackageName());
    }
    //#endregion

}
