package org.researchstack.backbone.storage.database.sqlite;
import co.touchlab.squeaky.db.sqlcipher.PassphraseProvider;

/**
 * Created by bradleymcdermott on 2/29/16.
 */
public class UpdatablePassphraseProvider implements PassphraseProvider
{

    private String passphrase = null;

    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    @Override
    public String getPassphrase()
    {
        return passphrase;
    }
}
