package ru.android.zheka.db

import com.activeandroid.Model
import com.activeandroid.query.Select
import com.google.android.gms.maps.model.LatLng
import java.util.*

object DbFunctions {
    const val DEFAULT_CONFIG_NAME = "defy"

    // see https://developers.google.com/maps/documentation/directions/intro#Restrictions
    const val AVOID_TOLLS = "tolls"
    const val AVOID_HIGHWAYS = "highways"
    const val AVOID_FERRIES = "ferries"
    const val AVOID_INDOR = "indoor"
    const val DEFAULT_CONFIG_OFFLINE = "false"

    @Deprecated("")
    fun getTraceByName(name: String?): Trace? {
        var out: Trace? = null
        try {
            out = Select()
                    .from(Trace::class.java)
                    .orderBy("RANDOM()")
                    .where("name=?", name)
                    .executeSingle()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    @JvmStatic
	@Deprecated("")
    fun getPointByName(name: String?): Point? {
        var out: Point? = null
        try {
            out = Select()
                    .from(Point::class.java)
                    .orderBy("RANDOM()")
                    .where("name=?", name)
                    .executeSingle()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    fun getNamePointByData(point: LatLng?): String {
        var out: Point? = null
        try {
            out = Select()
                    .from(Point::class.java)
                    .orderBy("RANDOM()")
                    .where("data=?", UtilePointSerializer().serialize(point!!) as String)
                    .executeSingle()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out?.name?:""
        }
    }

    @JvmStatic
	fun getModelByName(name: String?, cls: Class<out Model?>?): Model? {
        var out: Model? = null
        try {
            out = Select()
                    .from(cls)
                    .orderBy("RANDOM()")
                    .where("name=?", name)
                    .executeSingle()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    private fun getModelsByName(name: String?, cls: Class<out Model>): List<Model>? {
        var out: List<Model>? = null
        try {
            out = Select()
                    .from(cls)
                    .orderBy("RANDOM()")
                    .where("name=?", name)
                    .execute()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    fun getTableByModel(model: Model): Model? {
        val className = model.javaClass.name
        var out: Model? = null
        try {
            out = Select()
                    .from(Trace::class.java)
                    .where("$className = ? ", model.id)
                    .executeSingle()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    @JvmStatic
	fun getTablesByModel(class_: Class<out Model?>?): List<Model?>? {
        var out: List<Model?>? = null
        try {
            out = Select()
                    .from(class_)
                    .execute()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {
            return out
        }
    }

    @JvmStatic
	@Synchronized
    @Throws(IllegalAccessException::class, IllegalArgumentException::class, InstantiationException::class)
    fun add(table: Model): Long {
        //TODO recursive save calling for references
        val sTable = table.javaClass.newInstance()
        val fields = table.javaClass.declaredFields
        for (i in fields.indices) {
            val field = fields[i]
            field.isAccessible = true
            field[sTable] = field[table]
        }
        val name: String? = fields.find { it.name.equals("name") }?.get(table) as String
        val cls: Class<out Model> = table.javaClass
        val tables = getModelsByName(name, cls)
        /*while(name!=null && tables.size()>1 ){
			delete(tables.get(0));
			tables = getModelsByName(name, cls);
		}*/if (table.id != null) Model.delete(cls, table.id)
        if (tables!!.size > 0 && tables[0].id !== table.id) throw ConcurrentModificationException()
        if (tables.size > 1) throw ConcurrentModificationException("more then one db items with same name")
        return sTable.save()
    }

    fun delete(table: Model) {
        table.delete()
    }

    fun exsist(table: Model): Boolean {
        return table.id != null
    }

    @JvmStatic
	fun exsistPoint(name: String?): Boolean {
        val point = getPointByName(name)
        return if (point != null) true else false
    }

    fun exsistTrace(name: String?): Boolean {
        val trace = getTraceByName(name)
        return if (trace != null) true else false
    }
}