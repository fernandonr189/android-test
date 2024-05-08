package com.example.examen2.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.examen2.Libro
import com.example.examen2.R
import com.example.examen2.databinding.FragmentHomeBinding
import com.google.zxing.integration.android.IntentIntegrator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var codigo : Button
    private lateinit var buscar : Button
    private lateinit var guardar: Button

    private lateinit var Titulo: EditText
    private lateinit var Autor: EditText
    private lateinit var link: EditText

    private val binding get() = _binding!!
    private val listaDeLibros = mutableListOf<Libro>()
    private lateinit var scanLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codigo = view.findViewById(R.id.btnQR)
        buscar = view.findViewById(R.id.btnBuscar)
        guardar = view.findViewById(R.id.btnGuardar)

        Titulo = view.findViewById(R.id.edtTitulo)
        Autor = view.findViewById(R.id.edtAutor)
        link = view.findViewById(R.id.edtLink)

        codigo.setOnClickListener {
            escanearCodigoQR()
        }

        buscar.setOnClickListener {
            // Acción cuando se hace clic en el botón buscar         // Por ejemplo, buscar libros según un criterio
            buscar()
        }

        guardar.setOnClickListener {
            // Acción cuando se hace clic en el botón guardar
            // Por ejemplo, guardar los datos del libro en una base de datos
            guardar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        scanLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val intentData = result.data

                if (resultCode == Activity.RESULT_OK) {
                    val scanningResult =
                        IntentIntegrator.parseActivityResult(resultCode, intentData)
                    if (scanningResult != null) {
                        val scanContent =
                            scanningResult.contents // Contenido del código QR escaneado

                        // Comprobar si el contenido del código QR es un enlace válido
                        if (Patterns.WEB_URL.matcher(scanContent).matches()) {
                            link.setText(scanContent)
                        } else {
                            Toast.makeText(requireContext(), "Enlace no válido", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No se pudo escanear el código QR",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun escanearCodigoQR() {
        val intent = IntentIntegrator.forSupportFragment(this)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setPrompt("Escanea un código QR")
            .setOrientationLocked(false) // Permitir rotación de la cámara
            .setBeepEnabled(true) // Habilitar sonido de escaneo
            .createScanIntent()

        scanLauncher.launch(intent)
    }

    private fun guardar() {
        val titulo = Titulo.text.toString()
        val autor = Autor.text.toString()
        val link = link.text.toString()

        // Aquí puedes generar un ID automático, por ejemplo, utilizando la fecha actual en milisegundos
        val id = System.currentTimeMillis().toInt()

        // Crear una instancia de Libro con los datos ingresados en el formulario
        val libro = Libro(id, titulo, autor, link)

        // Agregar el libro a la lista de libros
        listaDeLibros.add(libro)

        // Aquí puedes realizar la acción de guardado, por ejemplo, guardar en una base de datos o en un archivo
        // Por ahora, simplemente mostraremos la información del libro en un Toast
        Toast.makeText(requireContext(), "Libro guardado:\n$libro", Toast.LENGTH_SHORT).show()
    }


    private fun buscar() {
        val tituloBusqueda = Titulo.text.toString()

        // Buscar el libro dentro del arreglo de libros
        val libroEncontrado = listaDeLibros.find { it.titulo == tituloBusqueda }

        // Verificar si se encontró el libro
        if (libroEncontrado != null) {
            // Si se encontró el libro, rellenar los campos de autor y enlace con los datos del libro encontrado
            Autor.setText(libroEncontrado.autor)
            link.setText(libroEncontrado.link)
        } else {
            // Si no se encontró el libro, mostrar un mensaje indicando que no se encontró
            Toast.makeText(requireContext(), "Libro no encontrado", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
