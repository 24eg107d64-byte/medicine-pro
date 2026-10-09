import React, { useEffect, useMemo, useState } from 'react';
import {
  Activity, ArrowDownUp, ArrowRight, BadgeCheck, Check, ChevronDown, Clock3,
  Cross, Heart, LogIn, MapPin, Menu, PackageCheck, Pill, Search,
  ShieldCheck, SlidersHorizontal, Sparkles, Store, Truck, UserRound, X,
} from 'lucide-react';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
const categories = ['All medicines', 'Pain relief', 'Cold & flu', 'Vitamins', 'Allergy', 'Digestive', 'Diabetes', 'Skin care', 'Antibiotics'];

async function request(path, options = {}) {
  const token = localStorage.getItem('medfind-token');
  const response = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
  const payload = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) throw new Error(payload?.message || payload?.error || 'Something went wrong. Please try again.');
  return payload;
}

function money(value) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value);
}

function App() {
  const [query, setQuery] = useState('');
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('All medicines');
  const [page, setPage] = useState(1);
  const [sort, setSort] = useState('price');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('medfind-user') || 'null'); } catch { return null; }
  });
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState('login');
  const [authError, setAuthError] = useState('');
  const [authBusy, setAuthBusy] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [expanded, setExpanded] = useState(null);
  const [alternatives, setAlternatives] = useState({});
  const [bookingBusy, setBookingBusy] = useState(null);
  const [checkoutOffer, setCheckoutOffer] = useState(null);
  const [checkoutAddress, setCheckoutAddress] = useState('');
  const [checkoutError, setCheckoutError] = useState('');
  const [notice, setNotice] = useState('');
  const [dashboard, setDashboard] = useState(false);
  const [myBookings, setMyBookings] = useState([]);
  const [pharmacyInventory, setPharmacyInventory] = useState([]);
  const [bookingError, setBookingError] = useState('');
  const [pharmacyError, setPharmacyError] = useState('');
  const [mobileMenu, setMobileMenu] = useState(false);

  const fetchResults = async (term = search) => {
    setLoading(true);
    setError('');
    try {
      const params = new URLSearchParams();
      if (term) params.set('q', term);
      const data = await request(`/medicines/search?${params.toString()}`);
      setResults(data || []);
    } catch (err) {
      setResults([]);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (search !== null) fetchResults(search);
  }, []);
  useEffect(() => {
    if (!notice) return undefined;
    const timeout = window.setTimeout(() => setNotice(''), 4200);
    return () => window.clearTimeout(timeout);
  }, [notice]);

  const filteredResults = useMemo(() => {
    let list = results.filter((item) => {
      if (category === 'All medicines') return true;
      return (item.category || '').toLowerCase().includes(category.toLowerCase().split(' ')[0]);
    });
    list = [...list].sort((a, b) => {
      if (sort === 'distance') return (a.distanceKm ?? 999) - (b.distanceKm ?? 999);
      if (sort === 'availability') return b.stock - a.stock;
      return a.price - b.price;
    });
    return list;
  }, [results, category, sort]);

  const medicines = useMemo(() => {
    const grouped = new Map();
    filteredResults.forEach((item) => {
      const key = item.medicineId;
      if (!grouped.has(key)) grouped.set(key, { ...item, offers: [] });
      grouped.get(key).offers.push(item);
    });
    return [...grouped.values()].map((medicine) => ({
      ...medicine,
      offers: medicine.offers.sort((a, b) => {
        if (sort === 'distance') return (a.distanceKm ?? 999) - (b.distanceKm ?? 999);
        if (sort === 'availability') return b.stock - a.stock;
        return a.price - b.price;
      }),
    }));
  }, [filteredResults, sort]);
  const pageSize = 12;
  const pageCount = Math.max(1, Math.ceil(medicines.length / pageSize));
  const visibleMedicines = medicines.slice((page - 1) * pageSize, page * pageSize);

  useEffect(() => { setPage(1); }, [search, category]);

  const doSearch = (event) => {
    event?.preventDefault();
    const term = query.trim();
    setSearch(term);
    setCategory('All medicines');
    setPage(1);
    fetchResults(term);
  };

  const authenticate = async (event) => {
    event.preventDefault();
    setAuthBusy(true);
    setAuthError('');
    try {
      const data = await request(`/auth/${authMode}`, {
        method: 'POST',
        body: JSON.stringify(authMode === 'register'
          ? { name: fullName, email, password }
          : { email, password }),
      });
      localStorage.setItem('medfind-token', data.token);
      localStorage.setItem('medfind-user', JSON.stringify(data.user));
      setUser(data.user);
      setAuthOpen(false);
      setPassword('');
      setNotice(`Welcome${data.user.name ? `, ${data.user.name}` : ''}! You are signed in.`);
    } catch (err) {
      setAuthError(err.message);
    } finally {
      setAuthBusy(false);
    }
  };

  const signOut = () => {
    localStorage.removeItem('medfind-token');
    localStorage.removeItem('medfind-user');
    setUser(null);
    setDashboard(false);
    setNotice('You have been signed out.');
  };

  const reserve = (offer) => {
    setCheckoutOffer(offer);
    setCheckoutAddress('');
    setCheckoutError('');
  };

  const placeOrder = async (event) => {
    event.preventDefault();
    if (!user) {
      setAuthMode('login');
      setAuthOpen(true);
      return;
    }
    if (user.role !== 'PATIENT') {
      setCheckoutError('Please sign in with a patient account to place a delivery order.');
      return;
    }
    if (!checkoutOffer) return;
    setBookingBusy(checkoutOffer.inventoryId);
    try {
      const booking = await request('/reservations', {
        method: 'POST',
        body: JSON.stringify({
          inventoryId: checkoutOffer.inventoryId,
          quantity: 1,
          deliveryAddress: checkoutAddress.trim(),
        }),
      });
      setNotice(`Order ${booking.code} confirmed. Pay ${money(booking.totalPrice)} in cash on delivery.`);
      setCheckoutOffer(null);
      setCheckoutAddress('');
      await fetchResults();
      if (user.role === 'PATIENT') {
        setMyBookings(await request('/reservations/mine'));
      }
    } catch (err) {
      setCheckoutError(err.message);
    } finally {
      setBookingBusy(null);
    }
  };

  const loadAlternatives = async (item) => {
    if (expanded === item.medicineId) {
      setExpanded(null);
      return;
    }
    setExpanded(item.medicineId);
    if (alternatives[item.medicineId]) return;
    try {
      const data = await request(`/medicines/${item.medicineId}/alternatives`);
      setAlternatives((current) => ({ ...current, [item.medicineId]: data || [] }));
    } catch (err) {
      setAlternatives((current) => ({ ...current, [item.medicineId]: { error: err.message } }));
    }
  };

  const loadBookings = async () => {
    setBookingError('');
    try {
      setMyBookings(await request('/reservations/mine'));
    } catch (err) {
      setBookingError(err.message);
    }
  };

  const loadPharmacyInventory = async () => {
    setPharmacyError('');
    try {
      setPharmacyInventory(await request('/pharmacy/inventory'));
    } catch (err) {
      setPharmacyError(err.message);
    }
  };

  const toggleDashboard = () => {
    const next = !dashboard;
    setDashboard(next);
    if (next) {
      loadBookings();
      if (user?.role === 'PHARMACIST') loadPharmacyInventory();
    }
  };

  const updateInventory = async (inventoryId, stock, price) => {
    try {
      await request(`/pharmacy/inventory/${inventoryId}`, {
        method: 'PUT',
        body: JSON.stringify({ stock: Number(stock), price: Number(price) }),
      });
      setNotice('Inventory updated successfully.');
      await loadPharmacyInventory();
      await fetchResults();
    } catch (err) {
      setNotice(err.message);
    }
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#" onClick={() => { setDashboard(false); setSearch(''); setQuery(''); fetchResults(''); }}>
          <span className="brand-mark"><Cross size={20} strokeWidth={3} /></span>
          <span>med<span>find</span></span>
        </a>
        <nav className={mobileMenu ? 'main-nav nav-open' : 'main-nav'}>
          <a className={!dashboard ? 'active' : ''} href="#search" onClick={() => { setDashboard(false); setMobileMenu(false); }}>Find medicine</a>
          {user && <button className={dashboard ? 'nav-button active' : 'nav-button'} onClick={toggleDashboard}>{user.role === 'PHARMACIST' ? 'Pharmacy dashboard' : 'My reservations'}</button>}
          <a href="#how-it-works" onClick={() => setMobileMenu(false)}>How it works</a>
        </nav>
        <div className="top-actions">
          <span className="location-button service-area"><MapPin size={16} /> <span>Pharmacies in Bengaluru</span></span>
          {user ? (
            <div className="account-actions">
              <span className="user-chip"><UserRound size={16} /> {user.name || user.email}</span>
              <button className="sign-out" onClick={signOut}>Sign out</button>
            </div>
          ) : <button className="sign-in" onClick={() => { setAuthMode('login'); setAuthOpen(true); }}><LogIn size={16} /> Sign in</button>}
          <button className="menu-button" aria-label="Open menu" onClick={() => setMobileMenu(!mobileMenu)}>{mobileMenu ? <X size={20} /> : <Menu size={20} />}</button>
        </div>
      </header>

      {!dashboard ? (
        <main>
          <section className="hero" id="search">
            <div className="hero-copy">
              <div className="eyebrow"><span className="live-dot" /> YOUR HEALTH, A LITTLE EASIER</div>
              <h1>Find your medicine.<br /><span>Skip the pharmacy runaround.</span></h1>
              <p>See what’s in stock nearby, compare prices, and reserve your medicine in just a few clicks.</p>
              <form className="search-box" onSubmit={doSearch}>
                <Search size={21} className="search-icon" />
                <input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Medicine name or prescription code" aria-label="Search medicines" />
                {query && <button className="clear-search" type="button" onClick={() => { setQuery(''); setSearch(''); fetchResults(''); }} aria-label="Clear search"><X size={17} /></button>}
                <button className="search-submit" type="submit">Search <ArrowRight size={17} /></button>
              </form>
              <div className="search-hint"><Sparkles size={14} /> Try “paracetamol”, “vitamin C” or “MED-001”</div>
              <div className="hero-trust">
                <span><ShieldCheck size={17} /> Verified pharmacies</span>
                <span><BadgeCheck size={17} /> Upfront prices</span>
                <span><Clock3 size={17} /> Live stock updates</span>
              </div>
            </div>
            <div className="hero-art" aria-hidden="true">
              <div className="art-orbit orbit-one" /><div className="art-orbit orbit-two" />
              <div className="art-sun" />
              <div className="medicine-box box-back"><span>VITAMIN C</span><div className="box-line" /><div className="box-cross">+</div></div>
              <div className="medicine-box box-front"><div className="box-top" /><span>DAILY CARE</span><strong>Wellness<br />starts here.</strong><div className="box-footer">30 TABLETS <span>✳</span></div></div>
              <div className="floating-card float-stock"><span className="stock-check"><Check size={14} /></span><div><strong>In stock nearby</strong><small>Ready when you are</small></div></div>
              <div className="floating-card float-price"><span className="price-spark">₹</span><div><strong>Best price found</strong><small>Compare & save more</small></div></div>
              <div className="art-pill pill-one" /><div className="art-pill pill-two" />
            </div>
          </section>

          <section className="finder-section" id="results">
            <div className="section-heading">
              <div>
                <div className="eyebrow section-kicker"><Activity size={15} /> PHARMACIES AROUND YOU</div>
                <h2>{search ? <>Results for <span>“{search}”</span></> : 'Find the right medicine'}</h2>
                <p>{search ? `${filteredResults.length} available offer${filteredResults.length === 1 ? '' : 's'} from verified local pharmacies` : 'Search by name or prescription code to compare local availability.'}</p>
              </div>
              <div className="result-controls">
                <label className="sort-control"><ArrowDownUp size={15} /><select value={sort} onChange={(e) => setSort(e.target.value)} aria-label="Sort results"><option value="price">Lowest price</option><option value="availability">Most in stock</option></select><ChevronDown size={14} /></label>
                <span className="radius-control"><Truck size={15} /> Delivery address at checkout</span>
              </div>
            </div>
            <div className="category-row">
              {categories.map((item) => <button key={item} className={`category-chip ${category === item ? 'selected' : ''}`} onClick={() => setCategory(item)}>{item}</button>)}
              <span className="filter-caption"><SlidersHorizontal size={14} /> Filter by category</span>
            </div>

            {error && <div className="error-banner"><span>{error}</span><button onClick={() => fetchResults()} aria-label="Retry"><ArrowRight size={16} /></button></div>}
            {loading ? <div className="loading-state"><span className="spinner" /> Finding nearby availability…</div> : medicines.length ? (
              <>
                <div className="medicine-grid">
                  {visibleMedicines.map((item) => <MedicineCard key={item.medicineId} item={item} expanded={expanded === item.medicineId} alternatives={alternatives[item.medicineId]} onAlternatives={() => loadAlternatives(item)} onReserve={reserve} bookingBusy={bookingBusy} />)}
                </div>
                {pageCount > 1 && <div className="pagination"><button disabled={page === 1} onClick={() => setPage(page - 1)}>Previous</button><span>Page {page} of {pageCount}</span><button disabled={page === pageCount} onClick={() => setPage(page + 1)}>Next</button></div>}
              </>
            ) : !error ? (
              <div className="empty-state"><span className="empty-icon"><Pill size={26} /></span><h3>{search ? 'No matching medicines found' : 'Your medicine search starts here'}</h3><p>{search ? 'Try another medicine name or prescription code, or remove the category filter.' : 'Search for a medicine above to see prices and availability near you.'}</p>{search && <button className="text-action" onClick={() => { setQuery(''); setSearch(''); setCategory('All medicines'); fetchResults(''); }}>Browse all medicines <ArrowRight size={15} /></button>}</div>
            ) : null}
            <div className="data-note"><ShieldCheck size={15} /> Availability and prices are provided by participating pharmacies and may change. Prescription medicines require pharmacist verification.</div>
          </section>

          <section className="how-section" id="how-it-works">
            <div className="how-heading"><div className="eyebrow"><Sparkles size={14} /> SIMPLE BY DESIGN</div><h2>Better medicine access,<br /><span>in three easy steps.</span></h2></div>
            <div className="steps">
              <div className="step-card"><span className="step-number">01</span><span className="step-icon"><Search size={22} /></span><h3>Search your medicine</h3><p>Look up a medicine by name, brand, or prescription code.</p></div>
              <div className="step-card"><span className="step-number">02</span><span className="step-icon"><ArrowDownUp size={22} /></span><h3>Compare nearby options</h3><p>Check local stock, distance, and clear prices side by side.</p></div>
              <div className="step-card"><span className="step-number">03</span><span className="step-icon"><Truck size={22} /></span><h3>Order for delivery</h3><p>Share your address and pay cash when your order arrives.</p></div>
            </div>
          </section>
          <section className="safety-strip"><span className="safety-icon"><Heart size={21} /></span><div><strong>Your health comes first.</strong><p>MedFind helps you locate medicine. Always follow your healthcare professional’s advice and prescription.</p></div><ShieldCheck size={21} className="safety-shield" /></section>
        </main>
      ) : (
        <main className="dashboard-page">
          <div className="dashboard-heading"><div className="eyebrow">{user?.role === 'PHARMACIST' ? <><Store size={15} /> PHARMACY PARTNER</> : <><PackageCheck size={15} /> YOUR ORDERS</>}</div><h1>{user?.role === 'PHARMACIST' ? 'Pharmacy dashboard' : 'My delivery orders'}</h1><p>{user?.role === 'PHARMACIST' ? 'Keep your availability and prices accurate for patients nearby.' : 'Track your medicine delivery orders and cash-on-delivery totals.'}</p></div>
          {user?.role === 'PHARMACIST' ? <div className="dashboard-grid">
            <section className="dashboard-card inventory-panel"><div className="panel-heading"><div><h2>Your inventory</h2><p>Update stock and pricing; changes are reflected in search results.</p></div><span className="verified-label"><BadgeCheck size={15} /> Verified account</span></div>{pharmacyError && <div className="error-banner">{pharmacyError}</div>}
              {pharmacyInventory.length ? pharmacyInventory.map((item) => <InventoryRow key={item.inventoryId} item={item} onSave={updateInventory} />) : <div className="empty-dashboard"><Store size={24} /><p>No inventory found for this pharmacy account.</p><button onClick={loadPharmacyInventory}>Refresh inventory</button></div>}
            </section>
            <section className="dashboard-card booking-panel"><div className="panel-heading"><div><h2>Incoming delivery orders</h2><p>Address and cash-on-delivery details for your orders.</p></div><button className="refresh-button" onClick={loadBookings}>Refresh</button></div>
              {bookingError && <div className="error-banner">{bookingError}</div>}
              {myBookings.filter((booking) => booking.pharmacyId === user?.pharmacyId).length ? myBookings.filter((booking) => booking.pharmacyId === user?.pharmacyId).map((booking) => <div className="booking-row" key={booking.id}><span className="booking-pills"><Pill size={17} /></span><div className="booking-info"><strong>{booking.medicineName}</strong><small>{booking.patientName} · Qty {booking.quantity} · COD {money(booking.totalPrice)}</small><small>{booking.deliveryAddress}</small></div><span className={`booking-status ${booking.status.toLowerCase()}`}>{booking.status}</span><small className="booking-code">{booking.code}</small></div>) : <div className="empty-dashboard"><PackageCheck size={24} /><p>No delivery orders yet.</p></div>}
            </section>
          </div> : <section className="dashboard-card patient-bookings"><div className="panel-heading"><div><h2>Your delivery orders</h2><p>Pay the displayed total in cash when your medicine is delivered.</p></div><button className="refresh-button" onClick={loadBookings}>Refresh</button></div>{bookingError && <div className="error-banner">{bookingError}</div>}{myBookings.length ? myBookings.map((booking) => <div className="patient-booking-row" key={booking.id}><span className="booking-pills"><Pill size={17} /></span><div className="booking-info"><strong>{booking.medicineName}</strong><small>{booking.pharmacyName} · Qty {booking.quantity} · {money(booking.totalPrice)}</small><small>{booking.deliveryAddress}</small></div><div className="pickup-code"><small>ORDER CODE</small><strong>{booking.code}</strong><small>{booking.paymentMethod === 'CASH_ON_DELIVERY' ? 'Cash on delivery' : booking.paymentMethod}</small></div><span className={`booking-status ${booking.status.toLowerCase()}`}>{booking.status}</span></div>) : <div className="empty-dashboard"><PackageCheck size={24} /><p>No delivery orders yet. Find a medicine and place an order.</p><button onClick={() => setDashboard(false)}>Find medicine</button></div>}</section>}
        </main>
      )}

      <footer className="footer"><a className="brand footer-brand" href="#"><span className="brand-mark"><Cross size={18} strokeWidth={3} /></span><span>med<span>find</span></span></a><span>Making medicine access a little easier.</span><span>© {new Date().getFullYear()} MedFind</span></footer>
      {notice && <div className="toast" role="status"><span className="toast-check"><Check size={14} /></span>{notice}<button onClick={() => setNotice('')} aria-label="Dismiss notification"><X size={15} /></button></div>}
      {checkoutOffer && <div className="modal-backdrop" onMouseDown={(e) => { if (e.target === e.currentTarget) setCheckoutOffer(null); }}><section className="auth-modal checkout-modal" role="dialog" aria-modal="true" aria-labelledby="checkout-title"><button className="modal-close" onClick={() => setCheckoutOffer(null)} aria-label="Close"><X size={20} /></button><div className="modal-icon"><Truck size={23} /></div><div className="eyebrow">DELIVERY ORDER</div><h2 id="checkout-title">Where should we deliver?</h2><p>{checkoutOffer.medicineName} from {checkoutOffer.pharmacyName} · {money(checkoutOffer.price)}</p>
        <form onSubmit={placeOrder} className="auth-form">
          <label>Delivery address<textarea required minLength={8} maxLength={400} value={checkoutAddress} onChange={(e) => setCheckoutAddress(e.target.value)} placeholder="Type your flat / house, street, landmark, city, PIN code" rows={3} /></label>
          <div className="payment-choice"><span className="cod-icon">₹</span><span><strong>Cash on delivery</strong><small>Pay {money(checkoutOffer.price)} in cash when it arrives. No online payment is accepted.</small></span><span className="cod-only">ONLY</span></div>
          {checkoutError && <div className="auth-error">{checkoutError}</div>}
          <button type="submit" className="auth-submit" disabled={bookingBusy === checkoutOffer.inventoryId}>{bookingBusy === checkoutOffer.inventoryId ? 'Placing order…' : 'Place COD order'} <ArrowRight size={17} /></button>
        </form>
        <div className="checkout-footnote"><ShieldCheck size={13} /> Your typed delivery address is shared with the selected pharmacy to arrange delivery.</div>
      </section></div>}
      {authOpen && <div className="modal-backdrop" onMouseDown={(e) => { if (e.target === e.currentTarget) setAuthOpen(false); }}><section className="auth-modal" role="dialog" aria-modal="true" aria-labelledby="auth-title"><button className="modal-close" onClick={() => setAuthOpen(false)} aria-label="Close"><X size={20} /></button><div className="modal-icon"><Cross size={23} /></div><div className="eyebrow">WELCOME TO MEDFIND</div><h2 id="auth-title">{authMode === 'login' ? 'Good to see you.' : 'Create your account.'}</h2><p>{authMode === 'login' ? 'Sign in to place delivery orders and manage them.' : 'Join MedFind to order medicine with cash on delivery.'}</p>
        <form onSubmit={authenticate} className="auth-form">
          {authMode === 'register' && <label>Your name<input required value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Alex Morgan" autoComplete="name" /></label>}
          <label>Email address<input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" autoComplete="email" /></label>
          <label>Password<input required minLength={6} type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="At least 6 characters" autoComplete={authMode === 'login' ? 'current-password' : 'new-password'} /></label>
          {authError && <div className="auth-error">{authError}</div>}
          <button type="submit" className="auth-submit" disabled={authBusy}>{authBusy ? 'Please wait…' : authMode === 'login' ? 'Sign in' : 'Create account'} <ArrowRight size={17} /></button>
        </form>
        <div className="auth-switch">{authMode === 'login' ? 'New to MedFind?' : 'Already have an account?'} <button onClick={() => { setAuthError(''); setAuthMode(authMode === 'login' ? 'register' : 'login'); }}>{authMode === 'login' ? 'Create account' : 'Sign in'}</button></div>
      </section></div>}
    </div>
  );
}

function MedicineCard({ item, expanded, alternatives, onAlternatives, onReserve, bookingBusy }) {
  const offer = item.offers[0];
  return (
    <article className="medicine-card">
      <div className="card-topline"><span className="medicine-category">{item.category || 'MEDICINE'}</span><span className="prescription-tag"><ShieldCheck size={13} /> {item.prescriptionRequired ? 'Prescription' : 'No prescription'}</span></div>
      <div className="medicine-info">
        <span className="medicine-thumb"><Pill size={25} /></span>
        <div className="medicine-title"><h3>{item.medicineName}</h3><p>{item.brand || 'Trusted generic'} · {item.dosage || 'Standard strength'}</p><span className="code-tag">{item.prescriptionCode || 'MED-' + String(item.medicineId).padStart(3, '0')}</span></div>
        <button className="favorite-button" aria-label={`Save ${item.medicineName}`} onClick={(e) => e.currentTarget.classList.toggle('favorited')}><Heart size={18} /></button>
      </div>
      <div className="price-summary"><div><small>FROM</small><strong>{money(offer.price)}</strong></div><span className="offers-count">{item.offers.length} {item.offers.length === 1 ? 'pharmacy' : 'pharmacies'}</span><span className="availability-pill"><span /> {offer.stock} in stock</span></div>
      <div className="pharmacy-offer"><div className="pharmacy-mark"><Store size={17} /></div><div className="pharmacy-detail"><strong>{offer.pharmacyName}</strong><span><MapPin size={12} /> {offer.distanceKm == null ? offer.pharmacyAddress : `${offer.distanceKm.toFixed(1)} km away`} · <Clock3 size={12} /> {offer.openingHours || 'Hours unavailable'}</span></div><strong className="offer-price">{money(offer.price)}</strong></div>
      {item.offers.length > 1 && <div className="other-offers">{item.offers.slice(1, 3).map((other) => <div className="other-offer" key={other.inventoryId}><span><Store size={13} /> {other.pharmacyName} <small>{other.distanceKm == null ? '' : `· ${other.distanceKm.toFixed(1)} km`}</small></span><strong>{money(other.price)}</strong><button disabled={!other.stock || bookingBusy === other.inventoryId} onClick={() => onReserve(other)}>{bookingBusy === other.inventoryId ? '…' : 'COD'}</button></div>)}</div>}
      <div className="card-actions"><button className="reserve-button" disabled={!offer.stock || bookingBusy === offer.inventoryId} onClick={() => onReserve(offer)}>{bookingBusy === offer.inventoryId ? 'Placing order…' : offer.stock ? <>Order · COD <Truck size={15} /></> : 'Out of stock'}</button><button className={`generic-button ${expanded ? 'is-open' : ''}`} onClick={onAlternatives}><Sparkles size={15} /> Generic alternatives <ChevronDown size={14} /></button></div>
      {expanded && <div className="alternatives-panel">{Array.isArray(alternatives) && alternatives.length ? alternatives.map((alt) => <div className="alternative-row" key={alt.medicineId}><span className="alt-icon"><Pill size={15} /></span><span><strong>{alt.medicineName}</strong><small>{alt.brand || 'Generic alternative'} · {alt.dosage || 'Same active ingredient'}</small></span><span className="alt-price">from {money(alt.lowestPrice)}</span></div>) : <p className="alternatives-message">{alternatives?.error || (alternatives ? 'No generic alternatives are listed for this medicine.' : 'Checking available alternatives…')}</p>}</div>}
      <div className="card-footnote"><ShieldCheck size={13} /> Prices confirmed by participating pharmacies <span>·</span> {item.offers.length} option{item.offers.length === 1 ? '' : 's'}</div>
    </article>
  );
}

function InventoryRow({ item, onSave }) {
  const [stock, setStock] = useState(item.stock);
  const [price, setPrice] = useState(item.price);
  const changed = Number(stock) !== item.stock || Number(price) !== item.price;
  return <div className="inventory-row"><div className="inventory-medicine"><span className="inventory-icon"><Pill size={17} /></span><div><strong>{item.medicineName}</strong><small>{item.brand || item.prescriptionCode}</small></div></div><label>Price (₹)<input type="number" min="0.01" step="0.01" value={price} onChange={(e) => setPrice(e.target.value)} /></label><label>Stock<input type="number" min="0" step="1" value={stock} onChange={(e) => setStock(e.target.value)} /></label><button className="save-stock" disabled={!changed} onClick={() => onSave(item.inventoryId, stock, price)}>{changed ? <><Check size={14} /> Save</> : 'Saved'}</button></div>;
}

export default App;
