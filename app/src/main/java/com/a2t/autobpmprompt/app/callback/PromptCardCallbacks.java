package com.a2t.autobpmprompt.app.callback;

import com.a2t.autobpmprompt.app.model.PromptSettings;

public interface PromptCardCallbacks {
    void onPromptSelected(PromptSettings prompt);
    void onRemovePromptClicked(PromptSettings prompt);
    void onActivePromptChanged(PromptSettings prompt);
}
