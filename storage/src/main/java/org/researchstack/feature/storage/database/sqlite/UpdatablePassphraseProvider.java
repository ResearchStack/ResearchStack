package org.researchstack.backbone.storage.database.sqlite;

import co.touchlab.squeaky.db.sqlcipher.PassphraseProvider;


public class UpdatablePassphraseProvider implements PassphraseProvider {

    private String passphrase = null;

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
