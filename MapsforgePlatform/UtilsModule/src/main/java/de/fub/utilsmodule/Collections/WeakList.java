/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.utilsmodule.Collections;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Serdar
 * @param <T>
 */
public class WeakList<T> implements List<T> {

    private final ArrayList<WeakReference<T>> weakReferences = new ArrayList<WeakReference<T>>();

    private WeakReference<T> createWeakReference(T ref) {
        return new WeakReference<T>(ref);
    }

    private void checkListAndRemove() {
        synchronized (weakReferences) {
            ArrayList<WeakReference<T>> arrayL = new ArrayList<WeakReference<T>>(weakReferences);
            for (WeakReference<T> ref : arrayL) {
                if (ref.get() == null) {
                    weakReferences.remove(ref);
                }
            }
            arrayL.clear();
        }
    }

    @Override
    public int size() {
        checkListAndRemove();
        return weakReferences.size();
    }

    @Override
    public boolean isEmpty() {
        checkListAndRemove();
        return weakReferences.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        synchronized (weakReferences) {
            for (WeakReference<T> weakRef : weakReferences) {
                if (weakRef.get() != null && weakRef.get().equals(o)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Iterator<T> iterator() {
        checkListAndRemove();
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return weakReferences.size() > i;
            }

            @Override
            public T next() {
                return weakReferences.get(i++).get();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    public Object[] toArray() {
        synchronized (weakReferences) {
            Object[] array = new Object[size()];
            for (int i = 0; i < array.length; i++) {
                WeakReference<T> ref = weakReferences.get(i);
                if (ref.get() != null) {
                    array[i] = ref.get();
                }
            }
            return array;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        synchronized (weakReferences) {
            ArrayList<T> elemArray = new ArrayList<T>();
            for (WeakReference<?> ref : weakReferences) {
                if (ref.get() != null) {
                    elemArray.add((T) ref.get());
                }
            }
            return elemArray.toArray(a);
        }
    }

    @Override
    public boolean add(T e) {
        synchronized (weakReferences) {
            return weakReferences.add(createWeakReference(e));
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (weakReferences) {
            int indexOf = indexOf(o);
            if (indexOf > -1) {
                return remove(indexOf) != null;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            if (!contains(c)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        synchronized (weakReferences) {
            boolean result = true;
            for (T element : c) {
                result &= add(element);
            }
            return result;
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        synchronized (weakReferences) {
            boolean result = true;
            for (T elem : c) {
                add(index++, elem);
            }
            return result;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        synchronized (weakReferences) {
            boolean result = true;
            for (Object o : c) {
                result &= remove(o);
            }
            return result;
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        weakReferences.clear();
    }

    @Override
    public T get(int index) {
        synchronized (weakReferences) {
            WeakReference<T> ref = weakReferences.get(index);
            if (ref.get() == null) {
                weakReferences.remove(ref);
            }
            return ref.get();
        }
    }

    @Override
    public T set(int index, T element) {
        assert element != null : "null elements are not supported!";
        synchronized (weakReferences) {
            return weakReferences.set(index, createWeakReference(element)).get();
        }
    }

    @Override
    public void add(int index, T element) {
        assert element != null : "null elements are not supported!";
        synchronized (weakReferences) {
            weakReferences.add(index, createWeakReference(element));
        }
    }

    @Override
    public T remove(int index) {
        synchronized (weakReferences) {
            WeakReference<T> ref = weakReferences.remove(index);
            return ref == null ? null : ref.get();
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (weakReferences) {
            for (WeakReference<T> ref : weakReferences) {
                if (ref.get() != null && ref.get().equals(o)) {
                    return weakReferences.indexOf(ref);
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        synchronized (weakReferences) {
            WeakReference<T> ref = null;
            for (int i = weakReferences.size() - 1; i > -1; i--) {
                ref = weakReferences.get(i);
                if (ref != null && ref.get() != null && ref.get().equals(o)) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        synchronized (weakReferences) {
            checkListAndRemove();
            return new ListIterator<T>() {
                private ListIterator<WeakReference<T>> it = weakReferences.listIterator();

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public T next() {
                    return it.next().get();
                }

                @Override
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }

                @Override
                public T previous() {
                    return it.previous().get();
                }

                @Override
                public int nextIndex() {
                    return it.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return it.previousIndex();
                }

                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public void set(T e) {
                    it.set(createWeakReference(e));
                }

                @Override
                public void add(T e) {
                    it.add(createWeakReference(e));
                }
            };

        }
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        synchronized (weakReferences) {
            checkListAndRemove();
            return new ListIterator<T>() {
                private ListIterator<WeakReference<T>> it = weakReferences.listIterator(index);

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public T next() {
                    return it.next().get();
                }

                @Override
                public boolean hasPrevious() {
                    return it.hasPrevious();
                }

                @Override
                public T previous() {
                    return it.previous().get();
                }

                @Override
                public int nextIndex() {
                    return it.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return it.previousIndex();
                }

                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public void set(T e) {
                    it.set(createWeakReference(e));
                }

                @Override
                public void add(T e) {
                    it.add(createWeakReference(e));
                }
            };
        }
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        synchronized (weakReferences) {
            List<WeakReference<T>> subList = weakReferences.subList(fromIndex, toIndex);
            ArrayList<T> arrayList = new ArrayList<T>();
            for (WeakReference<T> ref : weakReferences) {
                if (ref.get() != null) {
                    arrayList.add(ref.get());
                }
            }
            return arrayList;
        }
    }
}
