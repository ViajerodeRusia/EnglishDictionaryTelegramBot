package com.dailyenglish.EnglishDictionaryINTD.model;

import java.util.List;

public class DefinitionResponse {
    private List<Meaning> meanings;

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}
