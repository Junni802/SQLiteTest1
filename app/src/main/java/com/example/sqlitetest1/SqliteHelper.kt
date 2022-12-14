package com.example.sqlitetest1

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import org.w3c.dom.Text

class SqliteHelper(context: Context, name: String, version: Int): SQLiteOpenHelper(context, name, null, version) {
// SQLiteOpenHelper	: DB를 생성하고, 코틀린으로 DB를 사용할 수 있도록 연결하는 역할
	override fun onCreate(db: SQLiteDatabase?) {
	// 앱이 설치되어 SQLiteOpenHelper 클래스가 최초로 사용되는 순간 호출됨
	// 전체 앱에서 가장 처음 한 번만 수행되며, 대부분 테이블을 생성하는 코드를 작성
		val sql = "CREATE TABLE memo (" +
				"idx integer primary key, " +	// 일련번호(PK)
				"content text, " +
				"datetime integer" +
				");"
		db?.execSQL(sql)
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }
	// DB버전 정보가 변경될 때 마다 반복해서 호출되며, 테이블의 스키마 부분을 변경하기 위한 용도로 사용

		
	fun insertMemo(memo: Memo){
	// memo 테이블에 새로운 레코드를 Memo형 인스턴스로 받아 저장 시키는 함수
		val values = ContentValues()
		// ContentValues() : Map과 비슷한 형태로 키와 값의 쌍으로 데이터를 저장할 수 있음
		values.put("content", memo.content)
		values.put("datetime", memo.datetime)
		// 테이블에 넣을 값들을 컬럼명과 함께 저장

		val wd = writableDatabase
		wd.insert("memo", null, values)
		wd.close()
	}
	
	fun updateMemo(memo: Memo){
	// memo 테이블에 기존 레코드를 받아온 새로운 레코드로 변경하는 함수
		val values = ContentValues()
		// ContentValues() : Map과 비슷한 형태로 키와 값을 쌍으로 데이터를 저장할 수 있다
		values.put("content", memo.content)
		values.put("datetime", memo.datetime)
		// 테이블에서 변경할 값들을 컴럼명과 함께 저장

		val wd = writableDatabase
		wd.update("memo", values , "idx = ${memo.idx}", null)
		// wd.update("memo", values, "idx = ?", arrayOf("${memo.idx}")
		wd.close()
	}

	fun deleteMemo(memo: Memo){ 
	// memo 테이블에 조건에 맞는 특정 레코드(들)을 삭제하는 함수
		val wd = writableDatabase
		wd.delete("memo", "idx = ${memo.idx}", null)
		wd.close()
	}

	@SuppressLint("Range")
	fun selectMemo(): MutableList<Memo>{
	// memo 테이블의 레코드들을 모두 추출하여 리스트로 리턴하는 함수
		val list = mutableListOf<Memo>()
		val sql = "select * from memo"
		val rd = readableDatabase
		val rs = rd.rawQuery(sql, null)

		while (rs.moveToNext()) {
		// 	moveToNext() : 자바의 next()와 동일한 메소드로 커서를 다음 레코드로 내리면서 데이터 존재 여부를 리턴
			val idx = rs.getLong(rs.getColumnIndex("idx"))
			val content = rs.getString(rs.getColumnIndex("content"))
			val datetime = rs.getLong(rs.getColumnIndex("datetime"))
			list.add(Memo(idx, content, datetime))
		}
		rs.close()
		rd.close()

		return list
	}
}

data class Memo(var idx: Long?, var content: String, var datetime: Long){
// memo 테이블의 레코드 하나를 저장할 수 있는 데이터 클래스
// idx는 primary key이므로 자동증가값으로 설정되어 값이 없을 수도 있으므로 null값을 허용(?)
}