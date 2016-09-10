package com.a2t.autobpmprompt.app.callback;

import com.a2t.autobpmprompt.app.model.PromptSettings;

public interface SetListAdapterCallback {
    void onPromptSelected(String setList, PromptSettings prompt);
    void onCreatePromptClicked(String setList, int position);
    void onSetListRenamedClicked(String setList);
    void onRemoveSetListClicked(String setList);
    void onRemovePromptClicked(String setList, PromptSettings prompt, int position);
}
