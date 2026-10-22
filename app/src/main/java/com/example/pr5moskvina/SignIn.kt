package com.example.pr5moskvina

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson


class SignIn : Fragment() {

    private lateinit var btn: Button
    private lateinit var pass: EditText
    private lateinit var log: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_sign_in,
            container,
            false
        )
        btn=view.findViewById(R.id.btnEnter)
        pass=view.findViewById(R.id.edPass)
        log=view.findViewById(R.id.edLog)
        var prefs = requireContext().getSharedPreferences(
            "pp",
            Context.MODE_PRIVATE
        )
        btn.setOnClickListener {
            if (log.text.isNotEmpty() && pass.text.isNotEmpty()) {
                val user = User(
                    login = log.text.toString(), password = pass.text.toString()
                )

                val json = Gson().toJson(user)
                prefs.edit()
                    .putString("user", json)
                    .apply()

                Log.d("MyLog", "Es")
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        BlankFragment()
                    ).addToBackStack(null).commit()
            }
            else {
                Toast.makeText(requireContext(),"Ошибка", Toast.LENGTH_LONG)
            }
        }
        return view
    }


}