package ru.android.zheka.gmapexample1.edit;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;

public class EditModel implements Parcelable {
	public int nameId=0;
	public int name1Id=0;
	//Class <? extends Model> cls;
	public String clsName=null;
	public String clsPkg=null;

	private EditModel(Parcel in) {
		nameId = in.readInt();
		name1Id = in.readInt();
		clsName = in.readString();
		clsPkg = in.readString();
	}
	public EditModel() {}
	@Override
	public int describeContents() {
		if (clsName==null||nameId==0||name1Id==0)
			return -1;
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(nameId);
		dest.writeInt(name1Id);
		dest.writeString(clsName);
		dest.writeString(clsPkg);
		//dest.writeTypedObject(cls, 0);
	}
    public static final Creator CREATOR = new Creator() {
        public EditModel createFromParcel(Parcel in) {
            return new EditModel (in);
        }

        public EditModel[] newArray(int size) {
            return new EditModel[size];
        }
    };
	public Class<? extends Model> getClassTable(){
		String clsName = this.clsPkg+"."+this.clsName;
		clsName = clsName.replace("..", ".");
		Class<? extends Model> cls = null;
		try{cls = (Class<? extends Model>) Class.forName(clsName);
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return  cls;
	}
}
