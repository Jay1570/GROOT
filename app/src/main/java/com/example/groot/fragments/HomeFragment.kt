    package com.example.groot.fragments

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import com.example.groot.R
    import com.example.groot.RepoActivity
    import com.example.groot.StarredActivity
    import com.example.groot.viewmodel.ProfileViewModel
    import com.google.android.material.button.MaterialButton

    class HomeFragment : Fragment() {

        private lateinit var viewModel: ProfileViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
            val view = inflater.inflate(R.layout.fragment_home, container, false)
            val layRepository: MaterialButton = view.findViewById(R.id.btnRepository)
            val layStarred: MaterialButton = view.findViewById(R.id.btnStarred)

            var username = ""
            viewModel.profile.observe(viewLifecycleOwner) { username = it.userName + " " }

            layRepository.setOnClickListener {
                val intent = Intent(activity, RepoActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }

            layStarred.setOnClickListener {
                val intent = Intent(activity, StarredActivity::class.java)
                startActivity(intent)
            }

            return view
        }
    }