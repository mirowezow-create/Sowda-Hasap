package tm.sowdahasap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tm.sowdahasap.data.Repo
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.ui.AppTheme
import tm.sowdahasap.ui.CustomerDetailScreen
import tm.sowdahasap.ui.CustomersScreen
import tm.sowdahasap.ui.DebtSmsScreen
import tm.sowdahasap.ui.HomeScreen
import tm.sowdahasap.ui.InvoiceEditScreen
import tm.sowdahasap.ui.InvoicesScreen
import tm.sowdahasap.ui.LicenseScreen
import tm.sowdahasap.ui.LoginScreen
import tm.sowdahasap.ui.ProductEditScreen
import tm.sowdahasap.ui.ProductsScreen
import tm.sowdahasap.ui.ReportsScreen
import tm.sowdahasap.ui.SettingsScreen
import tm.sowdahasap.ui.StockScreen
import tm.sowdahasap.ui.UsersScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val ctx = LocalContext.current
                var license by remember { mutableStateOf(LicenseManager.status(ctx)) }
                LaunchedEffect(Unit) { Repo.ensureDefaults() }

                when {
                    !license.valid -> LicenseScreen(license) { license = LicenseManager.status(ctx) }
                    Session.user == null -> LoginScreen()
                    else -> MainNav(license, onLicense = { license = LicenseManager.status(ctx) })
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun MainNav(license: LicenseManager.Status, onLicense: () -> Unit) {
    val nav = rememberNavController()
    val back: () -> Unit = { nav.popBackStack() }
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(license, go = { nav.navigate(it) }) }
        composable("products") { ProductsScreen(back, open = { nav.navigate("product/$it") }) }
        composable("product/{id}") { ProductEditScreen(it.arguments?.getString("id") ?: "new", back) }
        composable("stock") { StockScreen(back) }
        composable("invoices") { InvoicesScreen(back, open = { id -> nav.navigate("invoice/$id") }) }
        composable("invoice/{id}") { InvoiceEditScreen(it.arguments?.getString("id") ?: "new", back) }
        composable("customers") { CustomersScreen(back, open = { nav.navigate("customer/$it") }) }
        composable("customer/{id}") {
            CustomerDetailScreen(it.arguments?.getString("id") ?: "new", back, openInvoice = { id -> nav.navigate("invoice/$id") })
        }
        composable("reports") { ReportsScreen(back) }
        composable("sms") { DebtSmsScreen(back) }
        composable("users") { UsersScreen(back) }
        composable("settings") { SettingsScreen(back, openLicense = { nav.navigate("license") }) }
        composable("license") { LicenseScreen(license, onBack = back) { onLicense(); back() } }
    }
}
