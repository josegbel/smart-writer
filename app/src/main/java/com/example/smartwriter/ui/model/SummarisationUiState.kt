package com.example.smartwriter.ui.model

data class SummarisationUiState(
    val inputText: String =
        "Title: The Rise of Urban Vertical Farming\n" +
            "\n" +
            "Urban vertical farming is emerging as a sustainable solution to meet the growing food demands of city populations. Unlike traditional agriculture, vertical farming relies on stacked layers of crops grown indoors under controlled conditions. This method allows year-round production, reduces water consumption by up to 90%, and eliminates the need for pesticides.\n" +
            "\n" +
            "By utilising hydroponic or aeroponic systems and LED lighting, vertical farms can optimise plant growth while minimising space. Many operations are set up in repurposed warehouses or shipping containers, often close to consumer markets, which significantly reduces transportation emissions and spoilage.\n" +
            "\n" +
            "Although initial setup costs remain high due to technology and energy requirements, advancements in automation and renewable energy integration are gradually improving cost efficiency. As climate change and urbanisation continue to pressure traditional farming systems, vertical farming represents a promising step toward more resilient and localised food production.",
    val summary: String = "",
    val isLoading: Boolean = false,
)
