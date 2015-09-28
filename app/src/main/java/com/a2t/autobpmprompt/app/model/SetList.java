package com.a2t.autobpmprompt.app.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SetList extends RealmObject {
    @PrimaryKey
    private String title;

    private RealmList<PromptSettings> prompts;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<PromptSettings> getPrompts() {
        return prompts;
    }

    public void setPrompts(RealmList<PromptSettings> prompts) {
        this.prompts = prompts;
    }


    public SetList() {

    }


}
