package com.cases.carefull.features.carefullcontents.feed.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cases.carefull.domain.model.SocialCategory
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcommon.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isEditMode = uiState.initialPost != null
    val initialPost = uiState.initialPost

    var title by remember(initialPost) { mutableStateOf(initialPost?.title ?: "") }
    var content by remember(initialPost) { mutableStateOf(initialPost?.content ?: "") }
    var selectedCategory by remember(initialPost) { mutableStateOf(SocialCategory.FREE) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val existingImageUrl by remember(initialPost) { mutableStateOf(initialPost?.imageUrl) }

    val categories = SocialCategory.entries.filter { it != SocialCategory.ALL }

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CustomTopAppBar(
            title = if (uiState.initialPost != null) stringResource(R.string.post_edit_title) else stringResource(
                R.string.post_create_new
            ),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back)
                    )
                }
            }
        )
        Card(
            modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth()
				.height(200.dp)
				.clickable { imagePickerLauncher.launch("image/*") },
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val displayImage = imageUri ?: existingImageUrl
                if (displayImage != null) {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = stringResource(R.string.content_description_post_image),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = stringResource(R.string.post_button_select_image),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(stringResource(R.string.post_label_select_image_optional))
                    }
                }
            }
        }
        Column(
            modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
				.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.post_label_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                    modifier = Modifier
						.menuAnchor()
						.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it.category) },
                            onClick = {
                                selectedCategory = it
                                isCategoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.post_label_title)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.post_label_content)) },
                modifier = Modifier
					.fillMaxWidth()
					.height(150.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitPost(title, content, selectedCategory, imageUri)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && title.isNotBlank() && content.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        if (isEditMode) stringResource(R.string.post_button_edit_complete) else stringResource(
                            R.string.post_button_create
                        )
                    )
                }
            }
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}