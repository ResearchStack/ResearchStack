package org.researchstack.sampleapp.bridge;
import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.List;

import co.touchlab.squeaky.db.sqlcipher.SQLiteDatabaseImpl;
import co.touchlab.squeaky.table.TableUtils;


public class BridgeEncryptedDatabase extends SqlCipherDatabaseHelper implements UploadQueue
{
    public BridgeEncryptedDatabase(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version, UpdatablePassphraseProvider passphraseProvider)
    {
        super(context, name, cursorFactory, version, passphraseProvider);
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
        super.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        // handle future db upgrades here
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
