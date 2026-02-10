package com.blackcat.android.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.blackcat.android.ui.screen.adddelivery.AddDeliveryScreen
import com.blackcat.android.ui.screen.deliverylist.DeliveryListScreen
import com.blackcat.android.ui.screen.detail.DeliveryDetailScreen
import com.blackcat.android.ui.screen.onboarding.OnboardingScreen
import com.blackcat.android.ui.screen.settings.SettingsScreen
import com.blackcat.android.ui.screen.splash.SplashScreen

@Composable
fun BlackCatNavHost(
    navController: NavHostController,
    startDestination: Screen = Screen.Splash
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
        }
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToDeliveryList = {
                    navController.navigate(Screen.DeliveryList) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.DeliveryList) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.DeliveryList> {
            DeliveryListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.DeliveryDetail(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddDelivery)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings)
                }
            )
        }

        composable<Screen.DeliveryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.DeliveryDetail>()
            DeliveryDetailScreen(
                deliveryId = route.deliveryId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.AddDelivery> {
            AddDeliveryScreen(
                onNavigateBack = { navController.popBackStack() },
                onDeliveryAdded = { navController.popBackStack() }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
