package com.example.myreciclerv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myreciclerv.Fragmentos.FragmentFlowerDetail
import com.example.myreciclerv.Fragmentos.FragmentFlowerList
import com.example.myreciclerv.data.Flower.Flower
import com.example.myreciclerv.data.Flower.flowerList

class MainActivity : AppCompatActivity(R.layout.layout_main_fragment){
    //lateinit var rvf: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main_fragment)

        val fcvl = findViewById<View>(R.id.fragment_container_view)
        val fcvf = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        if(fcvl != null && fcvf == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FragmentFlowerList>(R.id.fragment_container_view)
                //add(R.id.fragment_container_view, FragmentFlowerList())
            }
        }
        /*
        rvf = findViewById(R.id.recyclerView)

        rvf.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        val adapter = FlowerAdapter(
            flowerList(resources),{
                Toast.makeText(
                    applicationContext,
                    "Flor presionada ${it.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        rvf.adapter = adapter
        */
    }

    fun mostrarDetailFlower(flower: Flower) {

        val fcvl = findViewById<View>(R.id.fragment_container_view)
        val fcvf = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        if(fcvl != null &&  fcvf!=null ){

            val frag = FragmentFlowerDetail.newInstance(flower.id.toString(),"")
            val trans = supportFragmentManager.beginTransaction()
            trans.setReorderingAllowed(true)
            trans.replace(R.id.fragment_container_view, frag)
            trans.addToBackStack(null)
            trans.commit()
        }else{
            val fragD = supportFragmentManager.findFragmentById(R.id.fragdetail)
            fragD?.let {
                (it as FragmentFlowerDetail).setDetailFlower(flower.id)
            }
        }
    }

    //@RequiresApi(Build.VERSION_CODES.N)
    fun deleteFlower(id: Long) {
        /*DataSource.lsFlower.removeIf {
            it.id==id
        }*/

        var itemDF: Flower? = null
        for( fl in DataSource.lsFlower){
            if(fl.id == id ){
                itemDF = fl
            }
        }
        itemDF?.let {
            DataSource.lsFlower.remove(it)
        }

        val fcvl = findViewById<View>(R.id.fragment_container_view)
        val fcvf = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        if(fcvl!=null && fcvf !=null){
            supportFragmentManager.popBackStack()
        }
        else{
            DataSource.lsFlower.elementAtOrNull(0)?.let {
                val fragD = supportFragmentManager.findFragmentById(R.id.fragdetail) as FragmentFlowerDetail
                fragD?.setDetailFlower(it.id)

                val fragLs = supportFragmentManager.findFragmentById(R.id.fragls) as FragmentFlowerList
                fragLs?.updateLs()
            }
        }
    }
}
