import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './payment-success.component.html',
  styleUrls: ['./payment-success.component.scss']
})
export class PaymentSuccessComponent implements OnInit {

  orderId: string | null = null;

  constructor(private route: ActivatedRoute, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.orderId = this.route.snapshot.queryParamMap.get('orderId');
    this.cdr.detectChanges();
    // Redirection automatique après 5s
    setTimeout(() => {
      if (this.orderId) {
        this.router.navigate(['/orders', this.orderId]);
      } else {
        this.router.navigate(['/orders']);
      }
    }, 5000);
  }
}