package com.a2t.autobpmprompt.app.callback;

import com.a2t.autobpmprompt.app.model.PromptSettings;

public interface SetListAdapterCallback {
    void onPromptSelected(PromptSettings prompt);
    void onCreatePromptClicked(String setList, int position);
    void onSetListRenamedClicked(String setList);
    void onRemoveSetListClicked(String setList);
    void onRemovePromptClicked(PromptSettings prompt);
    void onActivePromptChanged(PromptSettings prompt);
}
