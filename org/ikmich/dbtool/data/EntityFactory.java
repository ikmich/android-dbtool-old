package org.ikmich.dbtool.data;

import org.ikmich.dbtool.DbRecord;
import org.ikmich.dbtool.DbRecordSet;
import org.ikmich.dbtool.Dbtool;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public abstract class EntityFactory implements IEntityFactory
{
	protected static Context context;
	protected static Dbtool dbtool;
	protected long lastCreatedRecordId = 0;

	public EntityFactory(Context c)
	{
		context = c;
		dbtool = Dbtool.getInstance(context);
		dbtool.setDb(getDb());
	}

	/**
	 * Gets the database name.
	 * 
	 * @return
	 */
	public abstract String getDb();

	/**
	 * Gets the table name for this entity.
	 * 
	 * @return
	 */
	public abstract String getTable();

	public abstract String getTag();

	@Override
	public boolean create(DbRecord row)
	{
		try
		{
			dbtool.openDb();

			lastCreatedRecordId = (Long) dbtool.insertInto(getTable()).record(row).run();
			return lastCreatedRecordId > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error creating ").append(getTag()).append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean update(long id, DbRecord record)
	{
		try
		{
			dbtool.openDb();

			int numRows = 0;
			dbtool.update(getTable());
			dbtool.record(record);
			dbtool.whereEquals(COL_ID, id);
			numRows = (Integer) dbtool.run();

			return numRows > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error updating ").append(getTag()).append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean delete(long id)
	{
		try
		{
			dbtool.openDb();
			int numRows = (Integer) dbtool.deleteFrom(getTable()).whereEquals(COL_ID, id).run();
			return numRows > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append(getTag()).append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean delete(String name, Object value)
	{
		try
		{
			dbtool.openDb();
			int n = (Integer) dbtool.deleteFrom(getTable()).whereEquals(name, value).run();
			return n > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append(getTag()).append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean delete(DbRecord record)
	{
		try
		{
			dbtool.openDb();
			int n = (Integer) dbtool.deleteFrom(getTable()).record(record).run();
			return n > 0;
		}
		catch (Exception ex)
		{
			alertError(new StringBuilder("Error deleting ").append("tag").append(": ").toString()
				+ ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public boolean hasItems()
	{
		try
		{
			dbtool.openDb();
			DbRecordSet records = (DbRecordSet) dbtool.get(COL_ID).from(getTable()).run();
			return records != null && records.size() > 0;
		}
		catch (Exception ex)
		{
			alertError("Error checking items availability: " + ex.getMessage());
			return false;
		}
		finally
		{
			dbtool.closeDb();
		}
	}

	@Override
	public long getLastCreatedRecordId()
	{
		return lastCreatedRecordId;
	}

	protected Context getContext()
	{
		return context;
	}

	protected void alert(Object msg)
	{
		alert(null, msg);
	}

	protected void alert(String title, Object msg)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
		b.setTitle(title).setMessage(msg.toString()).setPositiveButton("OK", null);
		AlertDialog d = b.create();
		d.show();
	}

	protected void alertError(Object msg)
	{
		alert("Error", msg);
	}

	protected void toast(Object msg)
	{
		Toast.makeText(getContext(), msg.toString(), Toast.LENGTH_SHORT).show();
	}

	protected void toast2(Object msg)
	{
		Toast.makeText(getContext(), msg.toString(), Toast.LENGTH_LONG).show();
	}
}
