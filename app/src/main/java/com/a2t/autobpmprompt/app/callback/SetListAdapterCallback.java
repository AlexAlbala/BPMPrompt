package com.a2t.autobpmprompt.app.callback;

import com.a2t.autobpmprompt.app.model.PromptSettings;

public interface SetListAdapterCallback {
    void onPromptSelected(String setList, PromptSettings prompt, int position);
    void onCreatePromptClicked(String setList);
    void onSetListRenamedClicked(String setList);
    void onSetListRemovedClicked(String setList);
}
