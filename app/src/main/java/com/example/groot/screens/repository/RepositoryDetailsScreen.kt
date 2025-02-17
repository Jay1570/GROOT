package com.example.groot.screens.repository

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.AppViewModelProvider
import com.example.groot.R
import com.example.groot.common.TopBar
import com.example.groot.model.Repository
import com.example.groot.ui.theme.GROOTTheme
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.Markwon
import okhttp3.internal.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailsScreen(
    navigateBack: () -> Unit,
    navigateToFileList: (String) -> Unit,
    viewModel: RepositoryDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior() // Enable scrolling behavior
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = true,
                navigateUp = navigateBack,
                containerColor = MaterialTheme.colorScheme.background,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        RepositoryDetailsContent(
            uiState = uiState,
            onCodeClick = navigateToFileList,
            onStarClick = viewModel::starOrUnstarRepo,
            scrollState = scrollState,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

@Composable
private fun RepositoryDetailsContent(
    uiState: RepoDetailsUiState,
    onCodeClick: (String) -> Unit,
    onStarClick: () -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(id = R.drawable.repo_git_svgrepo_com),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp).clip(CircleShape).border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = uiState.repository.owner, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = uiState.repository.name, fontSize = 30.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Icon(painterResource(id = R.drawable.starred), modifier = Modifier.size(24.dp), contentDescription = "Star")
                Spacer(Modifier.width(16.dp))
                Text(text = uiState.starCount.toString(), fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
            }

            Button(onClick = onStarClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.onSurface)) {
                Icon(painterResource(id = if (uiState.isStarred) R.drawable.filled_star else R.drawable.starred), contentDescription = "Star", tint = Color(0xFFDAA520))
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(if (uiState.isStarred) "Unstar" else "Star")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

            Card(modifier = Modifier.fillMaxWidth().clickable { onCodeClick("${uiState.repository.owner} / ${uiState.repository.name}") }, shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))) {
                        Icon(painterResource(id = R.drawable.code_square_svgrepo_com), contentDescription = "Code", modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Code", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
            Text(text = "Languages", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            LanguageBarChart(
                uiState.languageContributions.toMutableMap(),
                colors = listOf(
                    Color(0xffb07219),
                    Color(0xff945db7),
                    Color(0xff3572A5),
                    Color.LightGray
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer).padding(16.dp).fillMaxWidth(    )) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = R.drawable.info_rectangle_svgrepo_com), contentDescription = "ReadMe", modifier = Modifier.size(25.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "README", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.height(10.dp))
                val color = MaterialTheme.colorScheme.onSurface
                AndroidView(
                    factory = {
                        MaterialTextView(it).apply {
                            movementMethod = LinkMovementMethod.getInstance()
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            setTextColor(color.toArgb())
                        }
                    },
                    update = {
                        val markwon = Markwon.create(it.context)
                        markwon.setMarkdown(it, uiState.readmeContent)
                    }
                )
            }
        }
        if (uiState.inProcess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageBarChart(languageData: MutableMap<String, Int>, colors: List<Color>, modifier: Modifier = Modifier) {
    var othersTotal = languageData.remove("Others") ?: 0
    val sortedLanguages = languageData.entries.sortedByDescending { it.value }
    val topLanguages: MutableMap<String, Int> = sortedLanguages.take(3).associate { it.key to it.value }.toMutableMap()
    sortedLanguages.drop(3).forEach {
        othersTotal += it.value
    }
    if (othersTotal > 0) topLanguages["Others"] = othersTotal
    var total = 0
    topLanguages.entries.forEach { total += it.value }
    var start = 0F
    Column(Modifier.fillMaxHeight()) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.fillMaxWidth().height(20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                topLanguages.entries.forEachIndexed { index, entry ->
                    val proportion = entry.value.toFloat() / total
                    val color = colors[index]
                    val end = start + proportion * size.width
                    drawIntoCanvas { _ ->
                        withTransform({}) {
                            drawRect(
                                color = color,
                                topLeft = Offset(start, 0f),
                                size = Size(end - start, size.height)
                            )
                        }
                    }
                    start = end
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            topLanguages.entries.forEachIndexed { index, entry ->
                val color = colors.getOrElse(index) { Color.Gray }
                val percentage = format("%.2f", entry.value.toFloat() / total * 100)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(12.dp)
                            .background(color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${entry.key}(${percentage}%)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepoDetailsPreview() {
    GROOTTheme {
        Surface {
            RepositoryDetailsContent(
                uiState = RepoDetailsUiState(
                    repository = Repository(name = "Repository", owner = "Username"),
                    readmeContent = "This is README content",
                    starCount = 0,
                    languageContributions = mapOf(
                        Pair("Java", 20),
                        Pair("Kotlin", 80)
                    )
                ),
                onCodeClick = {},
                onStarClick = {},
                scrollState = rememberScrollState(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
