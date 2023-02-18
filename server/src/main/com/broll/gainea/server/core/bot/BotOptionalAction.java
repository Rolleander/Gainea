package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Action;

public abstract class BotOptionalAction<A extends NT_Action, O extends BotOptionalAction.BotOption> extends BotAction<A> {

    private O selectedOption;

    public abstract O score(A action);

    public void setSelectedOption(O selectedOption) {
        this.selectedOption = selectedOption;
    }

    public O getSelectedOption() {
        return selectedOption;
    }

    public static class BotOption {
        private float score;

        public BotOption(float score) {
            this.score = score;
        }

        public float getScore() {
            return score;
        }
    }

}
