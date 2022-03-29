package com.example.memesshare

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {

    var currentImageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadMeme()
    }

    // We make A Queue using singleton class created in which there are multiple
    // requests created by volley called from api

    private fun loadMeme()  {
        // Instantiate the RequestQueue.
        var progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val url = "https://meme-api.herokuapp.com/gimme"

        // Request a JSON response from the provided URL.

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,      //Here we requests from the url, if its a
                                            //success it gives a response otherwise gives an error
            { response ->
                currentImageUrl = response.getString("url")     //To extract url from json object
                var memeImageView: ImageView =findViewById(R.id.memeImageView)

                //Glide helps in putting the url to the image of our app by downloading it
                //Listener helps in hiding the progress bar after glide fetches and loads the meme
                Glide.with(this).load(currentImageUrl).listener(object: RequestListener<Drawable>{
                    override fun onLoadFailed(      //When loading of the image is failed
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(           //When the image is loaded
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
        //RequestListener is an interface which have 2 functions- OnLoadFailed & onResourceReady

                }).into(memeImageView)
            },
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        // For Adding the request to the RequestQueue.
    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)    //Action helps in connecting with other apps
                                        //on your phone like opening of a dialler or messaging app
        intent.type = "text/plain"      //Type of content you want to share
        intent.putExtra(Intent.EXTRA_TEXT,
            "Hey, checkout this cool meme i got form Reddit $currentImageUrl")
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)          //chooser lets you choose between apps you want to share on
    }
    fun nextMeme(view: View) {
        loadMeme()
    }
}