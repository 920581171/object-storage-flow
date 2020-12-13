package com.luoyk.osf.core.definition;

import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author luoyk
 */
public abstract class AbstractOsf {

    /**
     * 用于保存默认和自定义的操作流程
     */
    private final HashMap<Class<? extends Action>, Action> actionMap = new HashMap<>();

    public AbstractOsf() {
        actionMap.put(FileAction.class, fileActionProvider());
        actionMap.put(PictureAction.class, pictureActionProvider());
        for (Action action : customActionProvider()) {
            if (action instanceof FileAction || action instanceof PictureAction) {
                throw new RuntimeException("FileAction or PictureAction existed");
            }
            actionMap.put(action.getClass(), action);
        }
    }

    /**
     * 生成默认的TempId
     *
     * @return TempId
     */
    public String getTempId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @SuppressWarnings("unchecked")
    public <T extends Action> T getAction(Class<T> actionAchieveClass) {
        return (T) actionMap.get(actionAchieveClass);
    }

    public FileAction getFileAction() {
        return (FileAction) actionMap.get(FileAction.class);
    }

    public PictureAction getPictureAction() {
        return (PictureAction) actionMap.get(PictureAction.class);
    }


    protected abstract FileAction fileActionProvider();

    protected abstract PictureAction pictureActionProvider();

    protected List<Action> customActionProvider() {
        return Collections.emptyList();
    }
}
