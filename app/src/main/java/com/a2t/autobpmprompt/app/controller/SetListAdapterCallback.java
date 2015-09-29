package com.a2t.autobpmprompt.app.controller;

import com.a2t.autobpmprompt.app.model.PromptSettings;

public interface SetListAdapterCallback {
    void onPromptSelected(PromptSettings prompt, int position);
    void onCreatePromptClicked(String setList);
}
