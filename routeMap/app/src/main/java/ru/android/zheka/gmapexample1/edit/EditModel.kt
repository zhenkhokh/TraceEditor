package ru.android.zheka.gmapexample1.edit

import android.os.Parcel
import android.os.Parcelable
import com.activeandroid.Model

class EditModel : Parcelable {
    @kotlin.jvm.JvmField
    var nameId = 0
    @kotlin.jvm.JvmField
    var name1Id = 0

    //Class <? extends Model> cls;
    @kotlin.jvm.JvmField
    var clsName: String? = null
    @kotlin.jvm.JvmField
    var clsPkg: String? = null

    private constructor(`in`: Parcel) {
        nameId = `in`.readInt()
        name1Id = `in`.readInt()
        clsName = `in`.readString()
        clsPkg = `in`.readString()
    }

    constructor() {}

    override fun describeContents(): Int {
        return if (clsName == null || nameId == 0 || name1Id == 0) -1 else 0
    }

    override fun writeToParcel(dest: Parcel, arg1: Int) {
        dest.writeInt(nameId)
        dest.writeInt(name1Id)
        dest.writeString(clsName)
        dest.writeString(clsPkg)
        //dest.writeTypedObject(cls, 0);
    }

    val classTable: Class<out Model?>?
        get() {
            var clsName = clsPkg + "." + clsName
            clsName = clsName.replace("..", ".")
            var cls: Class<out Model?>? = null
            try {
                cls = Class.forName(clsName) as Class<out Model?>
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return cls
        }

    companion object {
        val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<Any?> {
            override fun createFromParcel(`in`: Parcel): EditModel? {
                return EditModel(`in`)
            }

            override fun newArray(size: Int): Array<EditModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}