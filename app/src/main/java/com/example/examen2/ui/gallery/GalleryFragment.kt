package com.example.examen2.ui.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.examen2.R
import com.example.examen2.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var foto: ImageView
    private lateinit var btnTomar: Button
    private lateinit var btnAgregar: Button
    private lateinit var btnMostrar: Button

    private var bitmapArray = ArrayList<Bitmap>()
    private var nombreApellidoArray = ArrayList<Pair<String, String>>()
    private var Index = 0 //Este es el indice actual de nuestro arreglo



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view: View = binding.root

        foto = view.findViewById(R.id.imageVFoto)
        btnTomar = view.findViewById(R.id.btnTomarFoto)
        nombre = view.findViewById(R.id.editNombre)
        apellido = view.findViewById(R.id.editApellido)



        //Metodos
        btnTomar.setOnClickListener {
//            Instancia para abrir la camara
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            Lo que sucede cuando la camara regresa un resultado
            responseLauncher.launch(intent)
        }

        btnAgregar = view.findViewById<Button>(R.id.btnAgregar) ?: Button(requireContext()) // Inicializa con un botón vacío si no se encuentra
        btnMostrar = view.findViewById<Button>(R.id.btnMostrat) ?: Button(requireContext()) // Inicializa con un botón vacío si no se encuentra

        btnAgregar.setOnClickListener {
            agregarNombreApellido()
        }

        btnMostrar.setOnClickListener {
            mostrarNombreApellido()
        }


        return view
    }

    // Método para agregar el nombre y apellido a la lista
    private fun agregarNombreApellido() {
        val nombreString = nombre.text.toString()
        val apellidoString = apellido.text.toString()
        nombreApellidoArray.add(Pair(nombreString, apellidoString))
        Toast.makeText(requireContext(), "Nombre y apellido agregados", Toast.LENGTH_SHORT).show()
    }

    // Método para mostrar los nombres y apellidos almacenados
    private fun mostrarNombreApellido() {
        val stringBuilder = StringBuilder()
        for ((nombre, apellido) in nombreApellidoArray) {
            stringBuilder.append("Nombre: $nombre\n")
            stringBuilder.append("Apellido: $apellido\n\n")
        }
        Toast.makeText(requireContext(), stringBuilder.toString(), Toast.LENGTH_LONG).show()
    }



    //Variable que se ejecuta una vez que tome la foto
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK){
            Toast.makeText(requireContext(),"Fotografia tomada", Toast.LENGTH_SHORT).show()
            val extras = activityResult.data?.extras
            val bitmap = extras?.get("data") as Bitmap?
            if(bitmap != null){
                bitmapArray.add(bitmap) //Agregamos a la lista de bitmapArray
                foto.setImageBitmap(bitmap) // Mostrar la ultima foto tomada
                Index = bitmapArray.size - 1 //Actualizamos el indice al ultimo elemento
            }else {
                Toast.makeText(requireContext(), "Error al obtener la imagen", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(),"Proceso cancelado", Toast.LENGTH_SHORT).show()
        }
    }//responseLauncher

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}