package com.example.myreciclerv.Fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myreciclerv.MainActivity
import com.example.myreciclerv.R
import com.example.myreciclerv.data.Flower.flowerList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentFlowerDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentFlowerDetail : Fragment() {
    lateinit var txtName: TextView
    lateinit var txtDescripcion: TextView
    lateinit var btnDelete: Button
    lateinit var img: ImageView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.layout_fragment_flower_detail, container, false)

        txtName = layout.findViewById <TextView> (R.id.flower_detail_name)
        txtDescripcion = layout.findViewById<TextView>(R.id.flower_detail_description)
        img = layout.findViewById<ImageView>(R.id.flower_detail_image)
        btnDelete = layout.findViewById<Button>(R.id.remove_button)
        param1?.let { setDetailFlower(it.toLong()) }

        return layout
    }

    fun setDetailFlower(id: Long) {
        val lsFlowers = flowerList(resources)
        val flower = lsFlowers.filter {  it.id==id }[0]
        txtName.text = flower.name
        txtDescripcion.text = flower.description
        img.setImageResource(flower.image?:R.drawable.ic_launcher_foreground)
        btnDelete.setOnClickListener {
            val actpapa = activity as MainActivity
            actpapa.deleteFlower(id)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentFlowerDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentFlowerDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}