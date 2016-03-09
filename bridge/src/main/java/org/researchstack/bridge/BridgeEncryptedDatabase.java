package org.researchstack.bridge;
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;

import java.sql.SQLException;
import java.util.List;

import co.touchlab.squeaky.db.sqlcipher.SQLiteDatabaseImpl;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by bradleymcdermott on 3/8/16.
 */
public class BridgeEncryptedDatabase extends SqlCipherDatabaseHelper
{
    public BridgeEncryptedDatabase(Context context, UpdatablePassphraseProvider passphraseProvider)
    {
        super(context, passphraseProvider);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        super.onCreate(sqLiteDatabase);
        try
        {
            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase), UploadRequest.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        // TODO properly handle upgrades without dropping the whole db
        try
        {
            TableUtils.dropTables(new SQLiteDatabaseImpl(sqLiteDatabase),
                    true,
                    UploadRequest.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
        onCreate(sqLiteDatabase);
    }

    public void saveUploadRequest(UploadRequest uploadRequest)
    {
        LogExt.d(this.getClass(), "saveUploadRequest() id: " + uploadRequest.id);

        try
        {
            this.getDao(UploadRequest.class).createOrUpdate(uploadRequest);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<UploadRequest> loadUploadRequests()
    {
        try
        {
            return this.getDao(UploadRequest.class).queryForAll().orderBy("id DESC").list();
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void deleteUploadRequest(UploadRequest request)
    {

        LogExt.d(this.getClass(), "deleteUploadRequest() id: " + request.id);

        try
        {
            this.getDao(UploadRequest.class).delete(request);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
