package ru.android.zheka.gmapexample1

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText

abstract class SaveDialog : DialogFragment() {
    //protected int hintId = R.string.hint_dialog_point;
    var nameField: EditText? = null
    private val DEFAULT_RES_ID = 0
    private val DEFAULT_RES_ID_STR = ""
    fun newInstance(hintId: Int): SaveDialog {
        println("start newInstance(int hintId)")
        println("get newInstance()")
        val frag = newInstance() //new SaveDialog();
        val args = Bundle()
        args.putString("hint", DEFAULT_RES_ID_STR)
        args.putInt("hint", hintId)
        frag.arguments = args
        println("end newInstance()")
        return frag
    }

    fun newInstance(hint: String?): SaveDialog {
        val frag = newInstance()
        val args = Bundle()
        args.putInt("hint", DEFAULT_RES_ID)
        args.putString("hint", hint)
        frag.arguments = args
        return frag
    }

    //get sub-class instance
    protected abstract fun newInstance(): SaveDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println("start onCreateDialog")
        super.onCreate(savedInstanceState)
        val inflater = activity.layoutInflater
        println("start getting builder")
        val builder = AlertDialog.Builder(activity)
        println("start setView")
        val view = inflater.inflate(R.layout.save_dialog, null)
        builder.setView(view)
        println("start getting nameField,builder is $builder")
        //builder.setView(R.layout.save_dialog);
        nameField = view.findViewById<View>(R.id.name_save_text) as EditText
        println("start getting resId")
        var resId = DEFAULT_RES_ID
        var hint = DEFAULT_RES_ID_STR
        try {
            resId = arguments.getInt("hint", DEFAULT_RES_ID)
        } catch (e: ClassCastException) {
            hint = try {
                arguments.getString("hint", DEFAULT_RES_ID_STR)
            } catch (ee: ClassCastException) {
                throw ClassCastException("cant define hint")
            }
        }
        println("start setHint,nameField is $nameField")
        if (resId != DEFAULT_RES_ID) nameField!!.setHint(resId) else println("resId is 0, try set string value ...")
        if (hint != DEFAULT_RES_ID_STR) nameField!!.hint = hint else println("hint is empty, see if resId was set up")
        println("start setNegativeButton")
        builder.setNegativeButton(R.string.cancel_save_point) { arg0, arg1 -> arg0.cancel() }
        println("start setPositiveButton")
        builder.setPositiveButton(R.string.ok_save_point) { arg0, arg1 ->
            positiveProcess()
            arg0.cancel()
        }
        println("start returning")
        return builder.create()
    }

    protected abstract fun positiveProcess() /*Intent intent = getActivity().getIntent();
		PositionUtil positionUtil = new PositionUtil();
		positionUtil.positionAndBoundInit(intent);
		String dataPoint = PositionUtil.latLngToString(positionUtil.getCenter());
		Toast.makeText(getActivity()
				, "save process as "
		+nameField.getText().toString()
		+", data: "
		+dataPoint
		+" is saved successfully", 15);
		*/
}