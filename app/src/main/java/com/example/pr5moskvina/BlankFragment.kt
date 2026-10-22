package com.example.pr5moskvina

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class BlankFragment : Fragment() {
    private lateinit var SerchMovie: EditText
    private lateinit var btn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var db: AppDatabase
    private lateinit var dao: MovieDao

    private val apiKey="2d07c869"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_blank,
            container,
            false
        )

        SerchMovie=view.findViewById(R.id.city)
        btn=view.findViewById(R.id.btnSearch)
        recyclerView=view.findViewById(R.id.recyclerView)

        db= AppDatabase.getDatabase(requireContext())
        dao=db.movieDao()

        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        adapter= MovieAdapter(emptyList(),
            onEditClick = {movie -> showEditDialog(movie)},
            onDeleteClick = {movie -> deleteMovie(movie)}
            )
        recyclerView.adapter = adapter

        loadMovies()
        btn.setOnClickListener {
            val movieName=SerchMovie.text.toString()
            if(movieName.isNotEmpty()&&movieName!=null){
            searchMovieRes(SerchMovie.text.toString())
            }
            else{
                var sn= Snackbar.make(requireView(), R.string.pods,
                    Snackbar.LENGTH_LONG)
            }
        }
        return view  }
    fun loadMovies(){
        CoroutineScope(Dispatchers.IO).launch {
            val movies=dao.getAll()
            requireActivity().runOnUiThread {
                adapter.updateList(movies)
            }
        }
    }
    fun searchMovieRes(movieName:String){
        val url="https://www.omdbapi.com/?apikey=$apiKey&s=$movieName"
        val queue= Volley.newRequestQueue(requireContext())
        val stringRequest= StringRequest(
            Request.Method.GET, url,
            {
                response->
                try{
                    val jsonObj= JSONObject(response)
                    if (jsonObj.getString("Response") == "True") {
                        val searchArray = jsonObj.getJSONArray("Search")
                        val firstMovie=searchArray.getJSONObject(0)
                        val title=firstMovie.getString("Title")
                        val year = firstMovie.getString("Year")
                        val imdbID = firstMovie.getString("imdbID")
                        saveMovie(Movie(title = title, year = year, imdbID = imdbID))
                    }
                    else{ Toast.makeText(requireContext(), "Фильм не найден", Toast.LENGTH_SHORT).show()}
                } catch (e: Exception){
                    Toast.makeText(requireContext(), "Ошибка парсинга: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            {
                var sn= Snackbar.make(requireView(), R.string.pods,
                    Snackbar.LENGTH_LONG)
            }
        )
        queue.add(stringRequest)
    }
    fun saveMovie(movie: Movie){
        CoroutineScope(Dispatchers.IO).launch {
            val existing = dao.getAll().find { it.imdbID == movie.imdbID }
            if (existing == null) {
                dao.insert(movie)
                loadMovies()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Фильм добавлен", Toast.LENGTH_SHORT).show()
                }
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Этот фильм уже есть в базе", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
     fun deleteMovie(movie: Movie) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(movie)
            loadMovies()
        }
    }
    fun showEditDialog(movie: Movie) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Редактировать фильм")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etTitle = EditText(requireContext())
        etTitle.setText(movie.title)
        layout.addView(etTitle)

        val etYear = EditText(requireContext())
        etYear.setText(movie.year)
        layout.addView(etYear)

        builder.setView(layout)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val newTitle = etTitle.text.toString()
            val newYear = etYear.text.toString()
            if (newTitle.isNotEmpty()) {
                val updatedMovie = movie.copy(title = newTitle, year = newYear)
                CoroutineScope(Dispatchers.IO).launch {
                    dao.update(updatedMovie)
                    loadMovies()
                }
            }
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

}