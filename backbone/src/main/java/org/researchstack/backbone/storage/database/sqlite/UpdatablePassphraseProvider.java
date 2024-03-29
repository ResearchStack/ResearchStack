package org.researchstack.backbone.storage.database.sqlite;


public class UpdatablePassphraseProvider {//implements PassphraseProvider

    private String passphrase = null;

    //@Override
    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
