import { useEffect, useState } from 'react';
import { createCategory, createCategoryGroup, fetchCategories, fetchCategoryGroups } from './productService';

export default function CategoryManager({ token, onClose }) {
  const [groups, setGroups] = useState([]);
  const [categories, setCategories] = useState([]);
  const [groupName, setGroupName] = useState('');
  const [categoryName, setCategoryName] = useState('');
  const [groupId, setGroupId] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [reload, setReload] = useState(0);

  useEffect(() => {
    let active = true;
    Promise.all([fetchCategoryGroups({ token }), fetchCategories({ token })])
      .then(([groupData, categoryData]) => {
        if (!active) return;
        setGroups(groupData || []);
        setCategories(categoryData || []);
        if (groupData?.length) setGroupId((current) => current || String(groupData[0].id));
      })
      .catch((requestError) => { if (active) setError(requestError.response?.data?.detail || 'No fue posible cargar categorías y grupos.'); });
    return () => { active = false; };
  }, [token, reload]);

  async function saveGroup(event) {
    event.preventDefault();
    setBusy(true); setError(''); setNotice('');
    try {
      const created = await createCategoryGroup({ token, name: groupName.trim() });
      setGroupName(''); setGroupId(String(created.id)); setNotice('Grupo de categorías creado.'); setReload((value) => value + 1);
    } catch (requestError) { setError(requestError.response?.data?.detail || 'No fue posible crear el grupo.'); }
    finally { setBusy(false); }
  }

  async function saveCategory(event) {
    event.preventDefault();
    setBusy(true); setError(''); setNotice('');
    try {
      await createCategory({ token, name: categoryName.trim(), groupId });
      setCategoryName(''); setNotice('Categoría creada.'); setReload((value) => value + 1);
    } catch (requestError) { setError(requestError.response?.data?.detail || 'No fue posible crear la categoría.'); }
    finally { setBusy(false); }
  }

  return <section className="products-category-manager">
    <div className="products-category-manager-heading"><div><h2>Grupos y categorías</h2><p>Las categorías se pueden asignar a los productos al registrarlos o editarlos.</p></div><button type="button" onClick={onClose}>Cerrar</button></div>
    {error && <p className="products-form-error" role="alert">{error}</p>}
    {notice && <p className="products-form-success" role="status">{notice}</p>}
    <div className="products-category-manager-grid">
      <form onSubmit={saveGroup}>
        <h3>Crear grupo</h3>
        <label>Nombre del grupo<input required maxLength="100" value={groupName} onChange={(event) => setGroupName(event.target.value)} /></label>
        <button type="submit" disabled={busy}>{busy ? 'Guardando…' : 'Crear grupo'}</button>
      </form>
      <form onSubmit={saveCategory}>
        <h3>Crear categoría</h3>
        <label>Grupo<select required value={groupId} onChange={(event) => setGroupId(event.target.value)}><option value="">Selecciona un grupo</option>{groups.map((group) => <option key={group.id} value={group.id}>{group.name}</option>)}</select></label>
        <label>Nombre de la categoría<input required maxLength="100" value={categoryName} onChange={(event) => setCategoryName(event.target.value)} /></label>
        <button type="submit" disabled={busy || !groups.length}>{busy ? 'Guardando…' : 'Crear categoría'}</button>
      </form>
    </div>
    <ul className="products-category-list">{categories.map((category) => <li key={category.id}>{category.name} · {groups.find((group) => group.id === category.groupId)?.name || `Grupo #${category.groupId}`}</li>)}</ul>
  </section>;
}
