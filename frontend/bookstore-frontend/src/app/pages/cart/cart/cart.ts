import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../../services/cart';
import Swal from 'sweetalert2';

interface CartItem {
  cartItemId: number;
  bookId: number;
  title: string;
  price: number;
  quantity: number;
  subtotal: number;
}

@Component({
  imports: [CommonModule, FormsModule],
  selector: 'app-cart',
  templateUrl: './cart.html',
  styleUrls: ['./cart.css'],
})
export class Cart implements OnInit {
  cartItems: CartItem[] = [];
  total: number = 0;

  constructor(private router: Router, private cartService: CartService) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cartItems = data.items || [];
        this.total = data.grandTotal || 0;
      },
      error: (err) => {
        console.error('Failed to load cart', err);
        Swal.fire({
          icon: 'error',
          title: 'Failed to load cart',
          text: 'Something went wrong while fetching your cart. Please try again later.',
        });
      },
    });
  }

  removeItem(cartItemId: number): void {
    this.cartService.removeFromCart(cartItemId).subscribe({
      next: () => {
        // Remove from local array after successful API delete
        this.cartItems = this.cartItems.filter(
          (item) => item.cartItemId !== cartItemId
        );
        this.total = this.cartItems.reduce(
          (acc, item) => acc + item.subtotal,
          0
        );
      },
      error: (err) => {
        console.error('Failed to remove item from cart', err);
        Swal.fire({
          icon: 'error',
          title: 'Failed to remove item',
          text: 'Something went wrong while removing the item from your cart. Please try again later.',
        });
      },
    });
  }

  clearCart(): void {
  Swal.fire({
    title: 'Are you sure?',
    text: 'This will remove all items from your cart.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Yes, clear it',
    cancelButtonText: 'Cancel',
  }).then((result) => {
    if (result.isConfirmed) {
      this.cartService.clearCart().subscribe({
        next: (response) => {
          if (response.status === 200 || response.status === 204) {
            this.cartItems = [];
            this.total = 0;
            Swal.fire('Cleared!', 'Your cart has been emptied.', 'success');
          } else {
            Swal.fire({
              icon: 'error',
              title: 'Unexpected response',
              text: `Received status: ${response.status}`,
            });
          }
        },
        error: (err) => {
          console.error('Failed to clear cart', err);
          Swal.fire({
            icon: 'error',
            title: 'Failed to clear cart',
            text: 'Something went wrong while clearing your cart. Please try again later.',
          });
        },
      });
    }
  });
}


 updateQuantity(cartItem: CartItem, newQuantity: string | number): void {
  const qty = Number(newQuantity);

  if (qty < 1) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid quantity',
      text: 'Quantity must be at least 1.',
    });
    return;
  }

  const payload = {
    cartItemId: cartItem.cartItemId,
    quantity: qty,
  };

  this.cartService.updateCartState(payload).subscribe({
    next: () => {
      this.loadCart(); // refresh totals
    },
    error: (err) => {
      console.error('Failed to update quantity', err);
      Swal.fire({
        icon: 'error',
        title: 'Update Failed',
        text: 'Something went wrong while updating the cart item quantity.',
      });
      this.loadCart(); // revert UI
    },
  });
}



  checkout(): void {
    this.router.navigate(['/checkout']);
  }
}
