package org.ikmich.dbtool.query;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;

public class UpdateQuery extends AbsQuery
{
	public UpdateQuery(SQLiteDatabase db)
	{
		this(null, db);
	}

	public UpdateQuery(Context c, SQLiteDatabase db)
	{
		super(c, db);
		_values = new ContentValues();
	}

	@Override
	public UpdateQuery context(Context c)
	{
		_context = c;
		return this;
	}

	void resetState()
	{
		_table = "";
		_whereClauses.clear();
		_whereConditions.clear();
		_whereClause = "";
	}

	public Integer run()
	{
		buildWhereClause();
		int numRows = _sqliteDb.update(_table, _values, _whereClause, _whereArgs);
		resetState();
		
		return numRows;
	}

}